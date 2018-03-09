package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lava.game.FloorIsLava;

/**
 * Created by moe on 08.03.18.
 */

public class MenuState extends State {

    public static FloorIsLava game;

    public MenuState(GameStateManager gsm, FloorIsLava game) {
        super(gsm);
        this.game = game;
        Gdx.gl.glClearColor(1, 0, 0, 1);

        Gdx.graphics.setWindowedMode(FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.update();



        // This might not work!!!
        //game.playServices.signIn();

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
