package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lava.game.FloorIsLava;
import com.lava.game.utils.SimpleDirectionGestureDetector;

public class EndState extends State {

    public static FloorIsLava game;

    private long result;
    private Boolean winner;
    private String resultText = "default";
    BitmapFont textFont = new BitmapFont();

    Stage stage;
    TextButton button1;
    TextButton.TextButtonStyle textButtonStyle1;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;
    public static String TAG = "LavaGame";
    private int buttonPressed;

    public EndState(GameStateManager gsm, FloorIsLava game, long result, Boolean winner){
        super(gsm);
        this.game = game;
        this.result = result;
        this.winner = winner;
        textFont.setColor(Color.WHITE);
        textFont.getData().scale(3f);

        Gdx.input.setCatchBackKey(true);
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
        button1 = new TextButton("Back", textButtonStyle1);
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
    }

    @Override protected void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            dispose();
            gsm.set(new MenuState(gsm, game));
        }
    }

    @Override public void update(float dt) {
        handleInput();
        if (winner){
            resultText = "winner winner chicken dinner! " + result;
        } else {
            resultText = "You LOST " + result;
        }

        switch (buttonPressed) {
            case 1:
                gsm.set(new MenuState(gsm, game));
                break;
        }
    }

    @Override public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);
        stage.draw();
        stage.act();
        sb.end();
        sb.begin();
        textFont.draw(sb, resultText, 20, FloorIsLava.HEIGHT/2-70, 600, FloorIsLava.WIDTH/2, true);
        sb.end();
    }

    @Override public void dispose() {

    }
}
