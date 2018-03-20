package com.lava.game.sprites;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.floor;

/**
 * Created by moe on 08.03.18.
 */

public class Board {

    private ArrayList<ArrayList<Tile>> board;

    /**
     * Create a board with given size
     * @param nTilesX
     * @param nTilesY
     */
    public Board(int nTilesX, int nTilesY) {
        board = new ArrayList<ArrayList<Tile>>();
        for (int y = 0; y < nTilesY; y++) {
            board.add(new ArrayList<Tile>());
            for (int x = 0; x < nTilesX; x++) {
                board.get(y).add(new Tile(this, x, y));
            }
        }
    }

    /**
     * Return's the board
     *
     * To get a tile, use: board.getBoard().get((yCoordinate).get(xCoordinate)
     * @return board
     */
    public ArrayList<ArrayList<Tile>> getBoard() {
        return board;
    }

    /**
     * Dispose of all tiles in the board
     */
    public void dispose() {
        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.get(r).size(); c++) {
                board.get(r).get(c).dispose();
            }
        }
    }


}
