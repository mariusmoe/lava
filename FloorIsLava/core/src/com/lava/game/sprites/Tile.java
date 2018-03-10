package com.lava.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.states.PlayState;

import java.lang.reflect.Array;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by moe on 08.03.18.
 */

public class Tile {

    private Board board;
    private Texture texture;
    public static int WIDTH = 16;
    public static int HEIGHT = 16;

    private int hp;
    private int xPos;
    private int yPos;
    private int halfLife;
    public static int deteriorationValue = 2;

    public Tile(Board board, int xPos, int yPos) {
        this.board = board;
        this.halfLife = random(1,5);
        this.hp = 100;
        this.xPos = xPos;
        this.yPos = yPos;
        this.texture = new Texture("TileA.png");
    }

    public int getHalfLife() {
        return halfLife;
    }

    public int getHp() {
        return hp;
    }

    public int[] getPos() {
        int [] res = new int[2];
        res[1] = xPos;
        res[2] = yPos;
        return res;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    /* If this method is called the tile will deteriorate */
    public void deteriorate() {
        int deterioration = halfLife * deteriorationValue;
        /*if (hp <= deterioration) {
            board.dispose(this);
        } else {*/
            hp -= deterioration;
            if (hp <= 75 && hp > 50) {
                texture = new Texture("TileA.png");
            } else if (hp <= 50 && hp > 25) {
                texture = new Texture("TileA2.png");
            } else if (hp <= 25 && hp > 0) {
                texture = new Texture("TileA4.png");
            } else if (hp < 0) {
                texture = new Texture("TileA6.png");
            }
        }
    //}

    public Texture getTexture() {
        return texture;
    }

}
