package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.FloorIsLava;

/**
 * Created by moe on 08.03.18.
 */

public class MenuState extends State {

    private Texture signIn;

    public static FloorIsLava game;

    public MenuState(GameStateManager gsm, FloorIsLava game) {
        super(gsm);
        this.game = game;
        Gdx.gl.glClearColor(1, 0, 0, 1);

        Gdx.graphics.setWindowedMode(FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.update();

        signIn = new Texture("button_task_1.png");

        // This might not work!!!
        //game.playServices.signIn();

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched()){
            Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
            //camera.unproject(tmp);

            Rectangle textureBounds=new Rectangle(320, FloorIsLava.HEIGHT-60,140,40);

            if (textureBounds.contains(tmp.x,tmp.y)){

                // game.playServices.startQuickGame(this);

                gsm.set(new PlayState(gsm));
                dispose();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(signIn, 320,20, 140, 40);
        sb.end();
    }

    @Override
    public void dispose() {
        signIn.dispose();
    }
}
