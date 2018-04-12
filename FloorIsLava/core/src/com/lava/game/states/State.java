package com.lava.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.FloorIsLava;

/**
 * Created by moe on 08.03.18.
 */

public abstract class State {

    protected OrthographicCamera cam;
    protected Vector3 mouse;
    protected GameStateManager gsm;

    protected State(GameStateManager gsm)   {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.update();
        mouse = new Vector3();


    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
}
