package com.lava.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {

    private Board board;
    public static FloorIsLava game;
    Boolean multiplayer;

    protected PlayState(GameStateManager gsm, FloorIsLava game, Boolean multiplayer) {
        super(gsm);
        this.game = game;
        this.multiplayer = multiplayer;
        this.board = new Board(10,20);
        Gdx.gl.glClearColor(1,0,0,1);
        //cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
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
