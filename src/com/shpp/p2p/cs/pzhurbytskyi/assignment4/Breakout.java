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
    private static final int NBRICKS_PER_ROW = 10;
    //Number of rows of bricks.
    private static final int NBRICK_ROWS = 5;

    //Separation between bricks.
    private static final int BRICK_SEP = 4;
    //Calculate Brick's width.
    private static final int BRICK_WIDTH = (APPLICATION_WIDTH - NBRICKS_PER_ROW * (BRICK_SEP + 1)) / NBRICKS_PER_ROW;
    //Height of a brick
    private static final int BRICK_HEIGHT = 8;
    //Offset of the top brick row from the top
    private static final int BRICK_Y_OFFSET = 70;
    //count of our bricks
    private static final int BRICKS_COUNTER = NBRICK_ROWS * NBRICKS_PER_ROW;

    //Radius of the ball in pixels
    private static final int BALL_RADIUS = 10;
    //Ball color
    private static final Color BALL_COLOR = Color.BLACK;

    //Number of turns
    private static final int NTURNS = 3;

    //Frame per second
    private static final double FPS = 1000 / 60.0;

    // x and y ball velocity
    private double vx, vy = 4;

    //The object which has been selected for dragging.
    private GObject object;
    // Rocket which we beat on ball
    private GRect racket;
    //Ball which can move
    private GOval ball;

    /**
     * In this method we're playing in game "Breakout"
     */
    public void run() {
        startGame(NTURNS);

    }

    /**
     * This method starting game Breakout:
     * create rocket, ball and bricks, which user must be destroyed for winning
     *
     * @param attempts count of ball "lives".
     */
    private void startGame(int attempts) {
        createWall(NBRICK_ROWS, NBRICKS_PER_ROW);
        addMouseListeners();
        createRacket();
        //play the game while we have attempts.
        while (attempts > 0) {
            createBallOnWindow();
            attempts = bounceBall(attempts, BRICKS_COUNTER);
            // if user destroy all bricks -> win, so we show to user that he/she win.
            if (BRICKS_COUNTER == 0 && attempts != 0) {
                createLabel("Congratulation!");
                return;
            }
            /*
            if ball fall, we make a little pause for user so that he/she
            has time to prepare for the next attempt.
             */
            pause(2000);
        }
        //If the attempts are over, it means that you have lost.
        createLabel("GAME OVER");
    }


    /**
     * This method creating racket on window program.
     */
    private void createRacket() {
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
    private void createBallOnWindow() {
        double x = getWidth() / 2.0 - BALL_RADIUS;
        double y = getHeight() / 2.0 - BALL_RADIUS;
        ball = createBall(x, y, BALL_RADIUS * 2, BALL_COLOR);
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
    private GOval createBall(double x, double y, double diameter, Color color) {
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
        if (object != null && object == racket) {
            //attach the center of racket to the cursor.
            double newX = e.getX() - object.getWidth() / 2.0;
            // if cursor beyond the window, racket stay in window
            if (newX < 0) {
                newX = 0;
            } else if (newX + object.getWidth() > getWidth()) {
                newX = getWidth() - PADDLE_WIDTH;
            }
            object.setLocation(newX, object.getY());
        }
    }


    /**
     * This method get an object which we hold down with the mouse.
     *
     * @param e the event to be processed
     */
    public void mousePressed(MouseEvent e) {
        object = getElementAt(e.getX(), e.getY());
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
        return ball.getX() + 2 * BALL_RADIUS >= getWidth() || ball.getX() <= 0;
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
     * @param attempts      starting count of attempts.
     * @param bricksCounter
     * @return count of attempts which are left.
     */
    private int bounceBall(int attempts, int bricksCounter) {

        //calculate the x velocity for to make the ball move in any direction at the beginning of the game.
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;

        boolean reboundFromRocket = true;
        while (true) {
            /* Move the ball by the current velocity. */
            ball.move(vx, vy);
            GObject collider = getCollidingObject(ball);

            if (collider == object && object != null) {
                vy = -vy;
                reboundFromRocket = false;
                while (ball.getY() + ball.getHeight() >= racket.getY()) {
                    ball.move(vx, vy);
                    pause(FPS);
                }
            } else if (collider != racket && collider != null) {
                vy = -vy;
                remove(collider);
                bricksCounter--;
                reboundFromRocket = false;
            } else if (ballOnTopBorder(ball)) {
                vy = -vy;
                reboundFromRocket = false;
            } else if (sideWallsBorder(ball)) {
                vx = -vx;
                reboundFromRocket = false;
            } else if (rightSideRocketBorder(ball)) {
                print("Yes");
                vx = -vx;
                reboundFromRocket = false;
            } else if (leftSideRocketBorder(ball)) {
                print("Yes2");
                vx = -vx;
                reboundFromRocket = false;
            }

            if (getCollidingObject(ball) == null && !sideWallsBorder(ball) && !ballOnTopBorder(ball)) {
                reboundFromRocket = true;
            }

            if (ballUnderTheBottomBorder(ball)) {
                attempts--;
                remove(ball);
                break;
            }

            if (bricksCounter == 0) {
                return attempts;
            }


            pause(FPS);
        }
        return attempts;
    }


    /**
     * This method check object Which the ball touched with one of its sides.
     *
     * @param ball our ball which we push.
     * @return object which the ball touched or null.
     */
    private GObject getCollidingObject(GOval ball) {
        //We try to get element in corners
        GObject topLeftCorner = getElementAt(ball.getX(), ball.getY());
        GObject topRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
        GObject bottomLeftCorner = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
        GObject bottomRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);

        //We try to get element on sides(to, right, left, bottom)
        GObject leftMiddlePoint = getElementAt(ball.getX() - 1, ball.getY() + BALL_RADIUS);
        GObject topMiddlePoint = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 1);
        GObject rightMiddlePoint = getElementAt(ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);
        GObject bottomMiddlePoint = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS + 1);

        //check every touch point
        if (topLeftCorner != null) {
            return topLeftCorner;
        } else if (topRightCorner != null) {
            return topRightCorner;
        } else if (bottomLeftCorner != null) {
            return bottomLeftCorner;
        } else if (bottomRightCorner != null) {
            return bottomRightCorner;
        } else if (leftMiddlePoint != null) {
            return leftMiddlePoint;
        } else if (topMiddlePoint != null) {
            return topMiddlePoint;
        } else if (rightMiddlePoint != null) {
            return rightMiddlePoint;
        } else {
            return bottomMiddlePoint;
        }

    }

    /**
     * This method creates a multilayer wall of bricks.
     *
     * @param rowNumber count of layers.
     * @param colNumber width of one layer.
     */
    private void createWall(int rowNumber, int colNumber) {
        for (int i = 0; i < rowNumber; i++) {
            createRow(i, colNumber);
        }
    }


    /**
     * This method create on layer of wall.
     *
     * @param currentLevel number of current layer
     * @param colNumber    width oo one layer.
     */
    private void createRow(int currentLevel, int colNumber) {
        for (int i = 0; i < colNumber; i++) {
            createBrick(i, currentLevel);
        }
    }

    /**
     * This method create bricks for the wall and add it to window.
     *
     * @param brickNumber number of brick in current layer.
     * @param layerNumber number of layer
     */
    private void createBrick(int brickNumber, int layerNumber) {
        //center the brick's x coord.
        double x = (getWidth() - (BRICK_WIDTH + BRICK_SEP) * NBRICKS_PER_ROW + BRICK_SEP) / 2.0;
        // for bricks on first layer y coord is a y-offset from top border of window
        double y = BRICK_Y_OFFSET;
        GRect brick = createGRect(x + brickNumber * (BRICK_WIDTH + BRICK_SEP),
                y + layerNumber * (BRICK_HEIGHT + BRICK_SEP),
                BRICK_WIDTH, BRICK_HEIGHT, Color.CYAN);
        add(brick);
    }

}
