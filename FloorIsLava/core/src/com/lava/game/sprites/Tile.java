package com.lava.game.sprites;

import java.lang.reflect.Array;

/**
 * Created by moe on 08.03.18.
 */

public class Tile {

    private Board board;

    private int hp;
    private int xPos;
    private int yPos;
    private int halfLife;
    public static int deteriorationValue = 10;

    public Tile(Board board, int xPos, int yPos, int halfLife) {
        this.board = board;
        this.halfLife = halfLife;
        this.hp = 100;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getHalfLife() {
        return halfLife;
    }

    public int getHp() {
        return hp;
    }

    public int[] getpos() {
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
        if (hp <= deterioration) {
            board.dispose(this);
        } else {
            hp -= deterioration;
        }
    }

}
