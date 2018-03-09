package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lava.game.FloorIsLava;
import com.badlogic.gdx.math.*;

/**
 * Created by moe on 08.03.18.
 */

public class MenuState extends State {
    Texture singlePlayer;
    Texture multiplayer;
    Vector3 tmp;

    private Texture signIn;
    private Texture singlePlayer;
    private Texture playMultiplayer;

    public static FloorIsLava game;

    public MenuState(GameStateManager gsm, FloorIsLava game) {
        super(gsm);
        this.game = game;
        //Gdx.gl.glClearColor(1, 0, 0, 1);

        //Gdx.graphics.setWindowedMode(FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        //cam.setToOrtho(false, FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        //cam.update();

        signIn = new Texture("button_task_1.png");
        singlePlayer = new Texture("button_task_2.png");
        playMultiplayer = new Texture("button_task_3.png");
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            tmp = new Vector3( Gdx.input.getX(), Gdx.input.getY(),0);
            cam.unproject(tmp);
            Rectangle textureBounds = new Rectangle((FloorIsLava.WIDTH/2) - (singlePlayer.getWidth()/2), FloorIsLava.HEIGHT / 2 + 160, singlePlayer.getWidth(), singlePlayer.getHeight());
            if(textureBounds.contains(tmp.x,tmp.y)) {
                gsm.set(new PlayState(gsm));
            }
        }
    }


    @Override
    public void update(float dt) {
        cam.update();
        if(Gdx.input.justTouched()){
            Vector3 tmp1 = new Vector3();
            cam.unproject(tmp1.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(220, 40,140,40);
            if (textureBounds.contains(tmp1.x,tmp1.y)){
                game.playServices.signIn();
                //dispose();
            }
        }
        if(Gdx.input.justTouched()){
            Vector3 tmp2 = new Vector3();
            cam.unproject(tmp2.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(400, 40,140,40);
            if (textureBounds.contains(tmp2.x,tmp2.y)){
                // single player
                gsm.set(new PlayState(gsm, game, false));
                dispose();
            }
        }
        if(Gdx.input.justTouched()){
            Vector3 tmp3 = new Vector3();
            cam.unproject(tmp3.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(580, 40,140,40);
            if (textureBounds.contains(tmp3.x,tmp3.y)){
                // multiplayer
                game.playServices.startQuickGame(this);

                // TODO: Add loading circle
            }
        }
    }

    public void startGame() {
        gsm.set(new PlayState(gsm, game, true));
        dispose();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);
        //Gdx.graphics.setWindowedMode(FloorIsLava.WIDTH, FloorIsLava.HEIGHT);
        cam.setToOrtho(false, FloorIsLava.WIDTH/2, FloorIsLava.HEIGHT / 2);
        cam.update();
        sb.draw(singlePlayer, (FloorIsLava.WIDTH/2) - (singlePlayer.getWidth()/2), FloorIsLava.HEIGHT / 2 + 160);
        sb.draw(multiplayer, (FloorIsLava.WIDTH/2) - (multiplayer.getWidth()/2), FloorIsLava.HEIGHT / 2 - 160);
        sb.end();
        sb.begin();
        sb.setProjectionMatrix(cam.combined);
        sb.draw(signIn, 220,40, 140, 40);
        sb.draw(singlePlayer, 400,40, 140, 40);
        sb.draw(playMultiplayer, 580,40, 140, 40);
        sb.end();
    }

    @Override
    public void dispose() {
        signIn.dispose();
        singlePlayer.dispose();
        playMultiplayer.dispose();
    }

}
