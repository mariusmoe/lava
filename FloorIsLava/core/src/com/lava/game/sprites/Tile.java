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

    private Board board;    // Is board needed?
    private Texture texture;
    public static int WIDTH = 16;   // Overridden by the draw method in playState
    public static int HEIGHT = 16;  // Overridden by the draw method in playState

    private int hp = 100;
    private int xPos;
    private int yPos;
    private int halfLife;
    private long timeBecameLava;
    public static int deteriorationValue = 2;

    public Tile(Board board, int xPos, int yPos) {
        this.board = board;
        this.halfLife = 5;      // random(1,5);
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
        int [] res = new int[1];
        res[0] = xPos;
        res[1] = yPos;
        return res;
    }

    // What are these for?
    public int getxPos() {
        return xPos;
    }
    public int getyPos() {
        return yPos;
    }

    /* If this method is called the tile will deteriorate */
    public void deteriorate() {
        int deterioration = halfLife * deteriorationValue;
        hp -= deterioration;
        if (hp <= 0 && timeBecameLava == 0){
            timeBecameLava = System.currentTimeMillis();
        }
    }

    public float getTimeTileBecameLava(){
        return timeBecameLava;
    }

    public void update() {
        if (hp <= 75 && hp > 50) {
            //texture.dispose();
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

    public void dispose() {
        texture.dispose();
    }
}
