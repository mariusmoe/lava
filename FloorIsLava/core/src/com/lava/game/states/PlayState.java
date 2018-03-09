package com.lava.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lava.game.FloorIsLava;
import com.lava.game.sprites.Player;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {

    public static FloorIsLava game;

    private Player playerOne;
    private Player playerTwo;

    Boolean multiplayer;

    protected PlayState(GameStateManager gsm, FloorIsLava game, Boolean multiplayer) {
        super(gsm);
        this.game = game;
        this.multiplayer = multiplayer;

        playerOne = new Player();
        if (multiplayer){
            playerTwo = new Player();
        }

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
