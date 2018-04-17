package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lava.game.FloorIsLava;
import com.badlogic.gdx.math.*;
import com.lava.game.utils.SimpleDirectionGestureDetector;

/**
 * Created by moe on 08.03.18.
 */

public class MenuState extends State {

    private Texture signIn;
    private Texture singlePlayer;
    private Texture playMultiplayer;
    public static String TAG = "LavaGame";
    boolean startMultiplayer = false;
    private int buttonPressed;


    Stage stage;
    TextButton button1;
    TextButton button2;
    TextButton button3;
    TextButton.TextButtonStyle textButtonStyle1;
    TextButton.TextButtonStyle textButtonStyle2;
    TextButton.TextButtonStyle textButtonStyle3;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;


    public static FloorIsLava game;

    public MenuState(GameStateManager gsm, FloorIsLava game) {
        super(gsm);
        this.game = game;
        Gdx.input.setCatchBackKey(true);

        // Again set the background color, why?
        //Gdx.gl.glClearColor(1, 0, 0, 1);

        // Need to set a input processor to be able to use isKeyJustPressed
        // took too long time to figure this out!
        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(
                new SimpleDirectionGestureDetector.DirectionListener() {
                    @Override
                    public void onUp() {

                    }
                    @Override
                    public void onRight() {

                    }
                    @Override
                    public void onLeft() {
                    }
                    @Override
                    public void onDown() {

                    }
                }));

        signIn = new Texture("button_task_1.png");
        singlePlayer = new Texture("button_task_2.png");
        playMultiplayer = new Texture("button_task_3.png");


        // Setup for buttons
        stage = new Stage(new FitViewport(FloorIsLava.WIDTH, FloorIsLava.HEIGHT));
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        skin = new Skin();
        // TODO: switch to gray buttons
        buttonAtlas = new TextureAtlas(Gdx.files.internal("ui-libgdx-20140505/ui-blue.atlas"));
        skin.addRegions(buttonAtlas);

        // Button 1
        // Holy macarooney - so much code for oe button
        textButtonStyle1 = new TextButton.TextButtonStyle();
        textButtonStyle1.font = font;
        textButtonStyle1.up = skin.getDrawable("button_01");
        textButtonStyle1.down = skin.getDrawable("button_02");
        textButtonStyle1.checked = skin.getDrawable("button_03");
        button1 = new TextButton("Singleplayer", textButtonStyle1);
        button1.setSize(240,80);
        button1.setPosition(FloorIsLava.WIDTH/2-120, 40);
        stage.addActor(button1);

        button1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(TAG,"Button pressed!!");
                buttonPressed = 1;
            }
        });

        // Button 2
        // Holy macarooney - so much code for oe button
        textButtonStyle2 = new TextButton.TextButtonStyle();
        textButtonStyle2.font = font;
        textButtonStyle2.up = skin.getDrawable("button_01");
        textButtonStyle2.down = skin.getDrawable("button_02");
        textButtonStyle2.checked = skin.getDrawable("button_03");
        button2 = new TextButton("Multiplayer", textButtonStyle2);
        button2.setSize(240,80);
        button2.setPosition(FloorIsLava.WIDTH/2-120, 240);
        stage.addActor(button2);

        button2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(TAG,"Button pressed!!");
                buttonPressed = 2;
            }
        });

        // Button 3
        // Holy macarooney - so much code for oe button
        textButtonStyle3 = new TextButton.TextButtonStyle();
        textButtonStyle3.font = font;
        textButtonStyle3.up = skin.getDrawable("button_01");
        textButtonStyle3.down = skin.getDrawable("button_02");
        textButtonStyle3.checked = skin.getDrawable("button_03");
        button3 = new TextButton("Login", textButtonStyle3);
        button3.setSize(240,80);
        button3.setPosition(FloorIsLava.WIDTH/2-120, 440);
        stage.addActor(button3);

        button3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(TAG,"Button pressed!!");
                buttonPressed = 3;
            }
        });
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            dispose();
            System.exit(0);     // Hack for avoiding context loss on onResume
            //Gdx.app.exit();
        }


    }


    @Override
    public void update(float dt) {
        cam.update();
        handleInput();
        if (startMultiplayer){
            Gdx.app.log(TAG,"Starting game!");
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
            gsm.set(new PlayState(gsm, game, true));
            dispose();
        }

        switch (buttonPressed) {
            case 1:
                // single player
                Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
                gsm.set(new PlayState(gsm, game, false));
                dispose();
                buttonPressed=0;
                break;
            case 2:
                game.playServices.startQuickGame(this);
                buttonPressed=0;
                break;
            case 3:
                game.playServices.signIn();
                buttonPressed=0;
                break;
        }

        // TODO: Clean up these buttons?

        /*if(Gdx.input.justTouched()){
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
        */
    }

    public void startGame() {
        // TODO: remove loading circle here
        startMultiplayer = true;
    }

    public void showWaitingRoom() {
        // TODO: add loading circle here;
        Gdx.app.log(TAG,"Waiting for game..." );
    }
    public void abortWaitingRoom() {
        // TODO: remove loading circle here;
        Gdx.app.log(TAG,"Waiting for game..." );
    }

    @Override
    public void render(SpriteBatch sb) {

        // // Could this prevent the clumsy solution with switch case?
        sb.begin();
        sb.setProjectionMatrix(cam.combined);
        stage.draw();
        stage.act();
        //sb.draw(signIn, FloorIsLava.WIDTH/2-70,40, 140, 40);
        //sb.draw(singlePlayer, FloorIsLava.WIDTH/2-70,140, 140, 40);
        //sb.draw(playMultiplayer, FloorIsLava.WIDTH/2-70,240, 140, 40);
        sb.end();
    }

    @Override
    public void dispose() {
        signIn.dispose();
        singlePlayer.dispose();
        playMultiplayer.dispose();
        stage.dispose();
    }

}
