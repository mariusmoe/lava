package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.sprites.Board;
import com.lava.game.FloorIsLava;
import com.lava.game.sprites.Player;
import com.lava.game.sprites.Tile;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {


    private Board board;
    private Player player;
    public static FloorIsLava game;
    Boolean multiplayer;

    protected PlayState(GameStateManager gsm, FloorIsLava game, Boolean multiplayer) {
        super(gsm);
        this.game = game;
        this.multiplayer = multiplayer;
        this.board = new Board(100 ,100);
        this.player = new Player(new Texture("pl.png"));
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        cam.update();
        player.update();
        Gdx.app.log("Player","xPos: " + player.getxPos() + " yPos: " + player.getyPos());
        board.getBoard().get(0).get(0).deteriorate();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();

        for (int r = 0; r < board.getBoard().size(); r++) {
            for (int c = 0; c < board.getBoard().get(r).size(); c++) {
                //Gdx.app.log("Tiles","x: " + board.getBoard().get(r).get(c).getxPos() + " y: " + board.getBoard().get(r).get(c).getyPos()+ " HL: " + board.getBoard().get(r).get(c).getHalfLife());
                sb.draw(board.getBoard().get(r).get(c).getTexture(),
                        board.getBoard().get(r).get(c).getxPos()* Tile.WIDTH,
                        board.getBoard().get(r).get(c).getyPos()*Tile.HEIGHT);
            }
        }
        sb.draw(player.getTexture(),player.getxPos(),player.getyPos(),16,16);
        sb.end();
    }

    @Override
    public void dispose() {

    }

}
