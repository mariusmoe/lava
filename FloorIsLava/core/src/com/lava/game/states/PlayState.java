package com.lava.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lava.game.FloorIsLava;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {
    protected PlayState(GameStateManager gsm) {

        super(gsm);
        cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
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
