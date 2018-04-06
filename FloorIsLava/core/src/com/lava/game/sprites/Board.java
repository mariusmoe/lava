package com.lava.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lava.game.states.PlayState;

import java.util.ArrayList;

/**
 * Created by moe on 08.03.18.
 */

public class Board {

    private ArrayList<ArrayList<Tile>> board;
    private Texture[] textures;
    private TextureRegion[] frames;
    private Animation<TextureRegion> animation;
    private float time;

    /**
     * Create a board with given size
     * @param nTilesX
     * @param nTilesY
     */
    public Board(int nTilesX, int nTilesY) {
        textures = new Texture[6];
        frames = new TextureRegion[6];
        textures[0] = new Texture("lavabg/lava_0.png");
        textures[1] = new Texture("lavabg/lava_1.png");
        textures[2] = new Texture("lavabg/lava_2.png");
        textures[3] = new Texture("lavabg/lava_3.png");
        textures[4] = new Texture("lavabg/lava_4.png");
        textures[5] = new Texture("lavabg/lava_5.png");
        for (int i = 0; i < 6; i++){
            textures[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            frames[i] = new TextureRegion(textures[i]);
            frames[i].setRegion(0, 0, nTilesX*PlayState.TILE_SIZE, nTilesY* PlayState.TILE_SIZE);
        }
        animation = new Animation<TextureRegion>(0.5f, frames);
        time = 0f;

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
     * UPSIDE DOWN IMPLEMENTATION! To get a tile, use:
     * board.getBoard().get((yCoordinate).get(xCoordinate)
     * @return board    The board created when starting a game
     */
    public ArrayList<ArrayList<Tile>> getBoard() {
        return board;
    }

    public void update(float dt) {
        /** Unnecessary?
        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.get(r).size(); c++) {
                board.get(r).get(c).update();
            }
        }
        **/
        time += dt;
    }

    /**
     * Dispose of all tiles in the board
     */
    public void dispose() {
        for (int i = 0; i < 6; i++){
            textures[i].dispose();
        }
        for (int r = 0; r < board.size(); r++) {
            for (int c = 0; c < board.get(r).size(); c++) {
                board.get(r).get(c).dispose();
            }
        }
    }

    public TextureRegion getBackground() {
        return animation.getKeyFrame(time, true);
    }

}
