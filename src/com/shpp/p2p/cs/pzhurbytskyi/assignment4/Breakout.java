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

    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    private static final Color PADDLE_COLOR = Color.BLACK;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 5;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 1;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * It's a bad idea to calculate brick width from APPLICATION_WIDTH
     */
    private static final int BRICK_WIDTH = (APPLICATION_WIDTH) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;
    private static final Color BALL_COLOR = Color.BLACK;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    private static final double FPS = 1000 / 60.0;

    private double vx, vy;


    /* The object which has been selected for dragging. */
    private GObject paddle;
    private GRect rocket;
    private GOval ball;
    private int bricksCounter = NBRICK_ROWS * NBRICKS_PER_ROW;


    /**
     *
     */
    public void run() {
        startGame(NTURNS);

    }

    /**
     * @param attempts
     */
    private void startGame(int attempts) {
        createWall(NBRICK_ROWS, NBRICKS_PER_ROW);
        addMouseListeners();
        createPaddle();
        while (attempts > 0) {
            createBallOnWindow();
            attempts = bounceBall(attempts);
            if (bricksCounter == 0 && attempts != 0) {
                createLabel("Congratulation!");
                return;
            }
            pause(3000);
        }
        createLabel("GAME OVER");
    }


    /**
     * This method creating paddle on window program.
     */
    private void createPaddle() {
        double x = (getWidth() - PADDLE_WIDTH) / 2.0;
        double y = getHeight() - PADDLE_Y_OFFSET;
        rocket = createGRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_COLOR);
        add(rocket);
    }

    private void createLabel(String text){
        GLabel label = new GLabel(text);
        label.setFont("Verdana-" + Math.min(APPLICATION_HEIGHT, APPLICATION_WIDTH) / 10);
        label.setColor(Color.RED);
        label.setLocation((getWidth() - label.getWidth()) / 2, getHeight() / 2.0);
        add(label);
    }

    private void createBallOnWindow() {
        double x = getWidth() / 2.0 - BALL_RADIUS;
        double y = getHeight() / 2.0 - BALL_RADIUS;
        ball = createBall(x, y, BALL_RADIUS * 2, BALL_COLOR);
        add(ball);
    }


    private GRect createGRect(double x, double y, double width, double height, Color color) {
        GRect gRect = new GRect(x, y, width, height);
        gRect.setFilled(true);
        gRect.setFillColor(color);
        return gRect;
    }

    private GOval createBall(double x, double y, double diameter, Color color) {
        GOval ball = new GOval(x, y, diameter, diameter);
        ball.setFilled(true);
        ball.setColor(color);
        return ball;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        /* If there is something to drag at all, go move it. */
        if (paddle != null && paddle == rocket) {
            double newX = e.getX() - paddle.getWidth() / 2.0;
            if (newX < 0) {
                newX = 0;
            } else if (newX + paddle.getWidth() > getWidth()) {
                newX = getWidth() - PADDLE_WIDTH;
            }
            paddle.setLocation(newX, paddle.getY());
        }
    }


    public void mousePressed(MouseEvent e) {
        paddle = getElementAt(e.getX(), e.getY());
    }


    private boolean ballOnTopBorder(GOval ball) {
        return ball.getY() <= 0;
    }

    private boolean sideWallsBorder(GOval ball) {
        return ball.getX() + 2 * BALL_RADIUS > getWidth()||ball.getX() <= 0;
    }



    private boolean ballUnderTheBottomBorder(GOval ball) {
        return ball.getY() >= getHeight();
    }


    private int bounceBall(int attempts) {
        vy = 3;
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;

        while (true) {
            /* Move the ball by the current velocity. */
            ball.move(vx, vy);
            GObject collider = getCollidingObject(ball);

            if (collider == paddle && paddle != null ) {
                vy = -vy;
            } else if (collider != rocket && collider != null) {
                vy = -vy;
                remove(collider);
                bricksCounter--;
            } else if (ballOnTopBorder(ball)) {
                vy = -vy;
            } else if (sideWallsBorder(ball)) {
                vx = -vx;
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
     * @return
     */
    private GObject getCollidingObject(GOval ball) {
        GObject topLeftCorner = getElementAt(ball.getX(), ball.getY());
        GObject topRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
        GObject bottomLeftCorner = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
        GObject bottomRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);

        if (topLeftCorner != null) {
            return topLeftCorner;
        } else if (topRightCorner != null) {
            return topRightCorner;
        } else if (bottomLeftCorner != null) {
            return bottomLeftCorner;
        } else return bottomRightCorner;
    }

    /**
     * @param rowNumber
     * @param colNumber
     */
    private void createWall(int rowNumber, int colNumber) {
        for (int i = 0; i < rowNumber; i++) {
            createRow(i, colNumber);
        }
    }


    /**
     * @param currentLevel
     * @param colNumber
     */
    private void createRow(int currentLevel, int colNumber) {
        for (int i = 0; i < colNumber; i++) {
            createBrick(i, currentLevel);
        }
    }

    private void createBrick(int brickNumber, int levelNumber) {
        double x = (getWidth() - NBRICKS_PER_ROW * BRICK_WIDTH) / 2.0;
        double y = BRICK_Y_OFFSET;
        GRect brick = createGRect(x + brickNumber * (BRICK_WIDTH),
                y + levelNumber * BRICK_HEIGHT,
                BRICK_WIDTH, BRICK_HEIGHT, Color.CYAN);
        add(brick);
    }

}
