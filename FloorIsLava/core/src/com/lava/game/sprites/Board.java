package com.lava.game.sprites;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Created by moe on 08.03.18.
 */

public class Board {

    private ArrayList<Tile> board;

    public Board(int size) {
        for (int n = 0; n < size; n++) {
            int x = nToXY(n,3)[0];
            int y = nToXY(n,3)[1];
            board.add(new Tile(this, x, y, random(1,5)));
        }
    }

    public Board(int nTilesX, int nTilesY) {
        for (int y = 0; y < nTilesY; y++) {
            for (int x = 0; x < nTilesX; x++) {
                board.add(new Tile(this, x, y, random(1,5)));
            }
        }
    }

    public void dispose(Tile tile) {
        int index = board.indexOf(tile);
        board.remove(index);
    }

    private int[] nToXY(int n, int width) {
        int[] res = new int[2];

        res[1] = n%width+1;
        res[2] = floor(n/width);
        return res;
    }

}
