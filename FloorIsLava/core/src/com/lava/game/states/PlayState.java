package com.lava.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lava.game.sprites.Board;
import com.badlogic.gdx.Gdx;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {

    private Board board;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        this.board = new Board(10,20);
        Gdx.gl.glClearColor(1,0,0,1);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        for (int r = 0; r < board.getBoard().size(); r++) {
            for (int c = 0; c < board.getBoard().get(r).size(); c++) {
                sb.draw(board.getBoard().get(r).get(c).getTexture(),
                        board.getBoard().get(r).get(c).getxPos()*16,
                        board.getBoard().get(r).get(c).getyPos()*16,
                        16,
                        16);
            }
        }
        sb.end();
    }

    @Override
    public void dispose() {

    }

}
