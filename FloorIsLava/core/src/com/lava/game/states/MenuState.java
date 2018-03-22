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

    private Texture signIn;
    private Texture singlePlayer;
    private Texture playMultiplayer;
    public static String TAG = "LavaGame";
    boolean startMultiplayer = false;


    public static FloorIsLava game;

    public MenuState(GameStateManager gsm, FloorIsLava game) {
        super(gsm);
        this.game = game;

        // Again set the background color, why?
        Gdx.gl.glClearColor(1, 0, 0, 1);

        signIn = new Texture("button_task_1.png");
        singlePlayer = new Texture("button_task_2.png");
        playMultiplayer = new Texture("button_task_3.png");
    }

    @Override
    protected void handleInput() {

    }


    @Override
    public void update(float dt) {
        cam.update();
        if (startMultiplayer){
            Gdx.app.log(TAG,"Starting game!");
            Gdx.gl.glClearColor(0, 0, 1, 1);
            gsm.set(new PlayState(gsm, game, true));
            dispose();
        }

        // TODO: Clean up these buttons, maybe use a scene?
        // TODO: move to handleInput?
        if(Gdx.input.justTouched()){
            Vector3 tmp1 = new Vector3();
            cam.unproject(tmp1.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(FloorIsLava.WIDTH/2-70, 40,140,40);
            if (textureBounds.contains(tmp1.x,tmp1.y)){
                game.playServices.signIn();
            }
        }
        if(Gdx.input.justTouched()){
            Vector3 tmp2 = new Vector3();
            cam.unproject(tmp2.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(FloorIsLava.WIDTH/2-70, 140,140,40);
            if (textureBounds.contains(tmp2.x,tmp2.y)){
                // single player
                Gdx.gl.glClearColor(0, 0, 1, 1);
                gsm.set(new PlayState(gsm, game, false));
                dispose();
            }
        }
        if(Gdx.input.justTouched()){
            Vector3 tmp3 = new Vector3();
            cam.unproject(tmp3.set(Gdx.input.getX(),Gdx.input.getY(),0));
            Rectangle textureBounds=new Rectangle(FloorIsLava.WIDTH/2-70, 240,140,40);
            if (textureBounds.contains(tmp3.x,tmp3.y)){
                // multiplayer
                game.playServices.startQuickGame(this);

                // TODO: Add loading circle
            }
        }
    }

    public void startGame() {
        startMultiplayer = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);
        sb.draw(signIn, FloorIsLava.WIDTH/2-70,40, 140, 40);
        sb.draw(singlePlayer, FloorIsLava.WIDTH/2-70,140, 140, 40);
        sb.draw(playMultiplayer, FloorIsLava.WIDTH/2-70,240, 140, 40);
        sb.end();
    }

    @Override
    public void dispose() {
        signIn.dispose();
        singlePlayer.dispose();
        playMultiplayer.dispose();
    }

}
