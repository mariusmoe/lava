package com.lava.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.lava.game.FloorIsLava;
import com.lava.game.states.PlayState;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by moe on 08.03.18.
 */

public class Player {

    private int xPos;   // x position of the player
    private int yPos;   // y position of the player
    private int receivedXPos;
    private int receivedYPos;
    private Texture texture;
    private Direction dir;  // the direction the player is facing
    private static final int MOVEMENT = 310;


    /**
     * Directly set the position of the player, should only be used for playerTwo
     * @param xPos  x position of the player
     * @param yPos  y position of the player
     */
    public void setPos(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Directly set the received position of the player, should only be used for playerTwo
     * @param X  received x position of the player
     * @param Y  received y position of the player
     */
    public void setReceivedPos(int X, int Y, int direction) {
        receivedXPos = X;
        receivedYPos = Y;
        switch (direction) {
            case 1:
                this.dir = Direction.NORTH;
                break;
            case 2:
                this.dir = Direction.EAST;
                break;
            case 3:
                this.dir = Direction.SOUTH;
                break;
            case 4:
                this.dir = Direction.WEST;
                break;
        }
    }

    public int getReceivedXPos() {return receivedXPos;}
    public int getReceivedYPos() {return receivedYPos;}

    public int getDir() {
        int direction = 1;
        switch (dir) {
            case NORTH:
                direction = 1;
                break;
            case EAST:
                direction = 2;
                break;
            case SOUTH:
                direction = 3;
                break;
            case WEST:
                direction = 4;
                break;
        }
        return direction;
    }


    private enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    public Player(int player) {
        if (player == 1){
            this.texture = new Texture("players/playerblue.png");
        } else {
            this.texture = new Texture("players/playergreen.png");
        }

        this.xPos = FloorIsLava.WIDTH/2;
        this.yPos = FloorIsLava.HEIGHT/2;

        int d = random(1,4);
        switch (d) {
            case 1:
                this.dir = Direction.NORTH;
                break;
            case 2:
                this.dir = Direction.EAST;
                break;
            case 3:
                this.dir = Direction.SOUTH;
                break;
            case 4:
                this.dir = Direction.WEST;
                break;
        }
    }

    public void update(float dt) {
        int distance = Math.round(MOVEMENT * dt);
        if (dir == Direction.NORTH){
            if ((yPos + distance) + (PlayState.PLAYER_HEIGHT) >= PlayState.BOARD_HEIGHT) {
                dir = Direction.SOUTH;
                yPos -= distance;
            } else {
                yPos += distance;
            }
        } else if (dir == Direction.EAST) {
            if ((xPos + PlayState.PLAYER_WIDTH + distance) >= FloorIsLava.WIDTH) {
                dir = Direction.WEST;
                xPos -= distance;
            } else {
                xPos += distance;
            }
        } else if (dir == Direction.SOUTH) {
            if ((yPos - distance) <= PlayState.CUTOFF_BOTTOM) {
                dir = Direction.NORTH;
                yPos += distance;
            } else {
                yPos -= distance;
            }
        } else if (dir == Direction.WEST) {
            if ((xPos - distance) <= 0) {
                dir = Direction.EAST;
                xPos += distance;
            } else {
                xPos -= distance;
            }
        }
    }

    public void turnUp()        { dir = Direction.NORTH; }

    public void turnRight()     { dir = Direction.EAST; }

    public void turnLeft()      { dir = Direction.WEST; }

    public void turnDown()      { dir = Direction.SOUTH; }

    public Texture getTexture() {
        return texture;
    }

    public int getxPos()        { return xPos; }

    public int getyPos()        { return yPos; }

    public void dispose()       { texture.dispose(); }
}
