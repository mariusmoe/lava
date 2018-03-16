package com.lava.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.lava.game.FloorIsLava;
import com.lava.game.states.PlayState;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by moe on 08.03.18.
 */

public class Player {

    private int xPos;
    private int yPos;
    private Texture texture;
    private Direction dir;
    private static final int MOVEMENT = 5;



    private enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    public Player(Texture tex) {
        this.texture = tex;
        this.xPos = random(texture.getWidth()/2, FloorIsLava.WIDTH-texture.getWidth()/2);
        this.yPos = random(texture.getHeight()/2, FloorIsLava.HEIGHT-texture.getHeight()/2);
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

    public void update() {
        if (dir == Direction.NORTH){
            if ((yPos + MOVEMENT) + (texture.getHeight()) >= PlayState.Y_TILES * 48 + PlayState.CUTOFF_BOTTOM) {
                dir = Direction.SOUTH;
                yPos -= MOVEMENT;
            } else {
                yPos += MOVEMENT;
            }
        } else if (dir == Direction.EAST) {
            if (xPos + (texture.getWidth()) >= FloorIsLava.WIDTH) {
                dir = Direction.WEST;
                xPos -= MOVEMENT;
            } else {
                xPos += MOVEMENT;
            }
        } else if (dir == Direction.SOUTH) {
            if ((yPos - MOVEMENT) + (texture.getHeight()/2) <= PlayState.CUTOFF_BOTTOM) {
                dir = Direction.NORTH;
                yPos += MOVEMENT;
            } else {
                yPos -= MOVEMENT;
            }
        } else if (dir == Direction.WEST) {
            if (xPos + (texture.getWidth()) <= 0) {
                dir = Direction.EAST;
                xPos += MOVEMENT;
            } else {
                xPos -= MOVEMENT;
            }
        }

    }

    public void turnUp() { dir = Direction.NORTH; }

    public void turnRight() { dir = Direction.EAST; }

    public void turnLeft() { dir = Direction.WEST; }

    public void turnDown() { dir = Direction.SOUTH; }

    public Texture getTexture() {
        return texture;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }
}
