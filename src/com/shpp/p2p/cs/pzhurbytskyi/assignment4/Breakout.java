package com.shpp.p2p.cs.pzhurbytskyi.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

// IN this class we play in game "Breakout"
public class Breakout extends WindowProgram {

    //Width and height of application window in pixels.
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    //Dimensions of the paddle.
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    //color of racket.
    private static final Color PADDLE_COLOR = Color.BLACK;
    //Offset of the paddle up from the bottom.
    private static final int PADDLE_Y_OFFSET = 30;

    //Number of bricks per row.
    private static final int N_BRICKS_PER_ROW = 10;
    //Number of rows of bricks.
    private static final int N_BRICK_ROWS = 1;

    //Separation between bricks.
    private static final int BRICK_SEP = 4;

    //Height of a brick
    private static final int BRICK_HEIGHT = 8;
    //Offset of the top brick row from the top
    private static final int BRICK_Y_OFFSET = 70;
    //count of our bricks
    private int bricksCounter = N_BRICK_ROWS * N_BRICKS_PER_ROW;

    //Radius of the ball in pixels
    private static final int BALL_RADIUS = 10;
    //Ball color
    private static final Color BALL_COLOR = Color.BLACK;

    //Wall colors
    private static final Color[] WALL_COLORS =
            new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};

    //Number of turns
    private static final int TURNS_NUMBER = 3;

    //Frame per second
    private static final double FPS = 1000 / 60.0;

    // x and y ball velocity
    private double vx, vy = 4;

    //The object which has been selected for dragging.
    private GObject controlObject;
    // Rocket which we beat on ball
    private GRect racket;


    /**
     * In this method we're playing in game "Breakout"
     */
    public void run() {
        startGame(TURNS_NUMBER);
    }

    /**
     * This method starting game Breakout:
     * create rocket, ball and bricks, which user must be destroyed for winning
     *
     * @param attempts count of ball "lives".
     */
    private void startGame(int attempts) {
        createWallOnWindow(N_BRICK_ROWS, N_BRICKS_PER_ROW);
        addMouseListeners();
        spawnRacketOnWindow();

        //play the game while we have attempts.
        GOval ball = createCircle(0, 0, BALL_RADIUS * 2, BALL_COLOR);
        while (attempts > 0) {
            waitForClick();
            spawnBallOnWindow(ball);
            attempts = bounceBall(ball, attempts);
            // if user destroy all bricks -> win, so we show to user that he/she win.
            if (bricksCounter == 0) {
                createLabel("Congratulation!");
                return;
            }
        }
        //If the attempts are over, it means that you have lost.
        createLabel("GAME OVER");
    }


    /**
     * This method creating racket on window program.
     */
    private void spawnRacketOnWindow() {
        double x = (getWidth() - PADDLE_WIDTH) / 2.0;
        double y = getHeight() - PADDLE_Y_OFFSET;
        racket = createGRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_COLOR);
        add(racket);
    }


    /**
     * This method create and show the end-game colored label.
     *
     * @param text text of label.
     */
    private void createLabel(String text) {
        GLabel label = new GLabel(text);
        label.setFont("Verdana-" + Math.min(APPLICATION_HEIGHT, APPLICATION_WIDTH) / 10);
        label.setColor(Color.RED);
        label.setLocation((getWidth() - label.getWidth()) / 2, getHeight() / 2.0);
        add(label);
    }

    /**
     * This method create the game ball and spawn it in center of window.
     */
    private void spawnBallOnWindow(GOval ball) {
        double x = getWidth() / 2.0 - BALL_RADIUS;
        double y = getHeight() / 2.0 - BALL_RADIUS;
        ball.setLocation(x, y);
        add(ball);
    }


    /**
     * This method create colored rectangle with params.
     *
     * @param x      x coord of future rectangle.
     * @param y      y coord of future rectangle.
     * @param width  width of rectangle.
     * @param height height of rectangle.
     * @param color  body color of rectangle.
     * @return created rectangle.
     */
    private GRect createGRect(double x, double y, double width, double height, Color color) {
        GRect gRect = new GRect(x, y, width, height);
        gRect.setFilled(true);
        gRect.setFillColor(color);
        gRect.setColor(Color.white);
        return gRect;
    }

    /**
     * This method create colored ball.
     *
     * @param x        x coord of future ball.
     * @param y        y coord of future ball.
     * @param diameter diameter of future ball.
     * @param color    body color of ball.
     * @return created ball with params.
     */
    private GOval createCircle(double x, double y, double diameter, Color color) {
        GOval ball = new GOval(x, y, diameter, diameter);
        ball.setFilled(true);
        ball.setColor(color);
        return ball;
    }


    /**
     * This method associates the racket movement with the mouse cursor.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // check if we hold the racket
        if (controlObject != null && controlObject == racket) {
            //attach the center of racket to the cursor.
            double newX = e.getX() - controlObject.getWidth() / 2.0;
            // if cursor beyond the window, racket stay in window
            if (newX < 0) {
                newX = 0;
            } else if (newX + controlObject.getWidth() > getWidth()) {
                newX = getWidth() - PADDLE_WIDTH;
            }
            controlObject.setLocation(newX, controlObject.getY());
        }
    }


    /**
     * This method get an object which we hold down with the mouse.
     *
     * @param e the event to be processed
     */
    public void mousePressed(MouseEvent e) {
        controlObject = getElementAt(e.getX(), e.getY());
    }


    /**
     * This method check if ball has touched top window border.
     *
     * @param ball ball which we're playing in game.
     * @return True if ball has touched the top border of window, or False if not.
     */
    private boolean ballOnTopBorder(GOval ball) {
        return ball.getY() <= 0;
    }

    /**
     * This method check if ball touched the right or the left borders of window.
     *
     * @param ball ball which we're playing in game.
     * @return True if ball has touched left or right window's border, or False if not.
     */
    private boolean sideWallsBorder(GOval ball) {
        return ball.getX() + ball.getWidth() >= getWidth() || ball.getX() <= 0;
    }

    /**
     * This method check if ball climbed on the racket or somehow got into it.
     *
     * @param x ball x coord
     * @param y ball y coord
     * @return True if ball into racket, otherwise False
     */
    private boolean isPaddleContainPoint(double x, double y) {
        return racket.getX() <= x && x <= racket.getX() + racket.getWidth() &&
                racket.getY() <= y && y <= racket.getY() + getHeight();
    }

    /**
     * This method checks whether the ball has touched the right side of the racket.
     *
     * @param ball ball which we're playing.
     * @return True if ball has touch to right side border of racket or False if not.
     */
    private boolean rightSideRocketBorder(GOval ball) {
        return isPaddleContainPoint(ball.getX(), ball.getY()) ||
                isPaddleContainPoint(ball.getX(), ball.getY() + ball.getHeight());
    }

    /**
     * This method checks whether the ball has touched the left side of the racket.
     *
     * @param ball ball which we're playing.
     * @return True if ball has touch to left side border of racket or False if not.
     */
    private boolean leftSideRocketBorder(GOval ball) {
        return isPaddleContainPoint(ball.getX() + ball.getWidth(), ball.getY()) ||
                isPaddleContainPoint(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight());
    }

    /**
     * This method check if ball fall beyond the bottom border.
     *
     * @param ball our ball which we play in game.
     * @return True if ball fall beyond the bottom border.
     * otherwise False that's mean we can play with tish ball yet.
     */
    private boolean ballUnderTheBottomBorder(GOval ball) {
        return ball.getY() >= getHeight();
    }

    /**
     * This method moving ball in cycle, and handles its collisions with various objects.
     *
     * @param attempts starting count of attempts.
     * @return count of attempts which are left.
     */
    private int bounceBall(GOval ball, int attempts) {
        //calculate the x velocity for to make the ball move in any direction at the beginning of the game.
        RandomGenerator randomGenerator = RandomGenerator.getInstance();
        vx = randomGenerator.nextDouble(1.0, 3.0);
        if (randomGenerator.nextBoolean(0.5))
            vx = -vx;
        while (true) {
            /* Move the ball by the current velocity. */
            ball.move(vx, vy);
            // handle ball collisions
            ballReflection(ball);
            // if ball fall decrease attempts.
            if (ballUnderTheBottomBorder(ball)) {
                attempts--;
                remove(ball);
                break;
            }
            //if we destroy all bricks -> we win.
            if (bricksCounter == 0) {
                return attempts;
            }
            pause(FPS);
        }
        return attempts;
    }

    /**
     * This method is responsible for the movement of the ball, its reflection from
     * the bricks of the walls and the racket.
     *
     * @param ball ball which we bounce.
     */
    private void ballReflection(GOval ball) {
        GObject collidingObj = getCollidingObject(ball);
        if (collidingObj == controlObject && controlObject != null) {
            reflectionFromTopRacketPart(ball);
        } else if (collidingObj != null) {
            vy = -vy;
            remove(collidingObj);
            bricksCounter--;
        } else if (ballOnTopBorder(ball)) {
            vy = -vy;
        } else if (rightSideRocketBorder(ball) || leftSideRocketBorder(ball)) {
            vx = -vx;
        } else if (sideWallsBorder(ball)) {
            reflectionFromWalls(ball);
        }
    }

    /**
     * This method bounces ball off the top part of racket.
     *
     * @param ball ball which we bounce.
     */
    private void reflectionFromTopRacketPart(GOval ball) {
        vy = -vy;
        while (ball.getY() + ball.getHeight() >= racket.getY()) {
            ball.move(vx, vy);
            pause(FPS);
        }
    }

    /**
     * This method bounces the ball off the walls.
     *
     * @param ball ball which we bounce.
     */
    private void reflectionFromWalls(GOval ball) {
        vx = -vx;
        while (sideWallsBorder(ball)) {
            ball.move(vx, vy);
            pause(FPS);
        }
    }

    /**
     * This method check object Which the ball touched with one of its sides.
     *
     * @param ball our ball which we push.
     * @return object which the ball touched or null.
     */
    private GObject getCollidingObject(GOval ball) {
        //We try to get element in corners
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                GObject corner = getElementAt(ball.getX() + 2 * BALL_RADIUS * i, ball.getY() + 2 * BALL_RADIUS * j);
                if (corner != null) {
                    return corner;
                }
            }
        }
        //We try to get element on sides(to, right, left, bottom)
        GObject leftMiddlePoint = getElementAt(ball.getX() - 1, ball.getY() + BALL_RADIUS);
        GObject topMiddlePoint = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 1);
        GObject rightMiddlePoint = getElementAt(ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);
        GObject bottomMiddlePoint = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS + 1);

        if (leftMiddlePoint != null) {
            return leftMiddlePoint;
        } else if (topMiddlePoint != null) {
            return topMiddlePoint;
        } else if (rightMiddlePoint != null) {
            return rightMiddlePoint;
        }
        return bottomMiddlePoint;

    }

    /**
     * This method creates a colored multilayer wall of bricks.
     *
     * @param rowNumber count of layers.
     * @param colNumber width of one layer.
     */
    private void createWallOnWindow(int rowNumber, int colNumber) {
        //number of rows which have same color.
        int n = (int) Math.ceil(Math.max(rowNumber / (double) WALL_COLORS.length, 1));
        //number of color in mas WALL_COLORS
        int colorNumber = 0;
        for (int i = 0; i < rowNumber; i++) {
            Color rowColor = WALL_COLORS[Math.min(colorNumber, WALL_COLORS.length - 1)];
            if ((i + 1) % n == 0) {
                colorNumber++;
            }
            createRow(i, colNumber, rowColor);
        }
    }


    /**
     * This method create layer of wall.
     *
     * @param currentLevel number of current layer
     * @param colNumber    width oo one layer.
     * @param currentColor color of layer.
     */
    private void createRow(int currentLevel, int colNumber, Color currentColor) {
        for (int i = 0; i < colNumber; i++) {
            createBrick(i, currentLevel, currentColor);
        }
    }

    /**
     * This method create colored bricks for the wall and add it to window.
     *
     * @param brickNumber  number of brick in current layer.
     * @param layerNumber  number of layer.
     * @param currentColor color of bricks for this layer.
     */
    private void createBrick(int brickNumber, int layerNumber, Color currentColor) {
        //Calculate Brick's width.
        int brickWidth = (getWidth() - (N_BRICKS_PER_ROW - 1) * BRICK_SEP) / N_BRICKS_PER_ROW;
        //center the brick's x coord.
        double x = (getWidth() - (brickWidth + BRICK_SEP) * N_BRICKS_PER_ROW + BRICK_SEP) / 2.0;
        // for bricks on first layer y coord is a y-offset from top border of window
        int y = BRICK_Y_OFFSET;
        GRect brick = createGRect(x + brickNumber * (brickWidth + BRICK_SEP),
                y + layerNumber * (BRICK_HEIGHT + BRICK_SEP),
                brickWidth, BRICK_HEIGHT, currentColor);
        add(brick);
    }

}
