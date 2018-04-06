package com.lava.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.states.PlayState;

import java.lang.reflect.Array;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by moe on 08.03.18.
 */

public class Tile {

    private Board board;    // Is board needed?
    private Random random = new Random();
    private int tileType = 1;
    public static int WIDTH = 16;   // Overridden by the draw method in playState
    public static int HEIGHT = 16;  // Overridden by the draw method in playState
    private Texture currentTexture;
    private Texture[] textures;
    private static Texture[] TEXTURES1 = {
            new Texture("tiles/tile01.png"),
            new Texture("tiles/tile12.png"),
            new Texture("tiles/tile13.png"),
            new Texture("tiles/tile14.png"),
            new Texture("tiles/tile15.png"),
            new Texture("tiles/tile06.png")
    };
    private static Texture[] TEXTURES2 = {
            new Texture("tiles/tile01.png"),
            new Texture("tiles/tile22.png"),
            new Texture("tiles/tile23.png"),
            new Texture("tiles/tile24.png"),
            new Texture("tiles/tile25.png"),
            new Texture("tiles/tile06.png")
    };

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
        tileType = random.nextInt(2) + 1;
        textures = new Texture[6];
        switch (tileType) {
            case 1:
                textures = TEXTURES1;
                break;
            case 2:
                textures = TEXTURES2;
        }
        currentTexture = textures[0];
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
        if (hp <= 80 && hp > 60 && currentTexture != textures[1]) {
            currentTexture = textures[1];
        } else if (hp <= 60 && hp > 40 && currentTexture != textures[2]) {
            currentTexture = textures[2];
        } else if (hp <= 40 && hp > 20 && currentTexture != textures[3]) {
            currentTexture = textures[3];
        } else if (hp <= 20 && hp > 0 && currentTexture != textures[4]) {
            currentTexture = textures[4];
        } else if (hp <= 0 && currentTexture != textures[5]) {
            currentTexture = textures[5];
        }
    }
    //}

    public Texture getTexture() {
        return currentTexture;
    }

    public void dispose() {
        /* for (int i = 0; i < textures.length; i++) {
            textures[i].dispose();
        } */
    }
}
