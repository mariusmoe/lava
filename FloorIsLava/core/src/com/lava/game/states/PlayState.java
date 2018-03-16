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
import com.lava.game.utils.SimpleDirectionGestureDetector;

/**
 * Created by moe on 08.03.18.
 */

public class PlayState extends State {


    private Board board;

    public static FloorIsLava game;
    public static int X_TILES = 10;
    public static int Y_TILES = 12;
    public static int CUTOFF_BOTTOM = 80;
    public static String TAG = "LavaGame";

    private float collector = 0;

    private Player playerOne;
    private Player playerTwo;

    Boolean multiplayer;

    protected PlayState(GameStateManager gsm, FloorIsLava game, Boolean multiplayer) {
        super(gsm);
        this.game = game;
        this.multiplayer = multiplayer;
        this.board = new Board(X_TILES ,Y_TILES);

        playerOne = new Player(new Texture("pl.png"));
        if (multiplayer){
            playerTwo = new Player(new Texture("pl.png"));
        }

        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                // TODO Auto-generated method stub
                Gdx.app.log(TAG,"MAgic - swiped up");
                playerOne.turnUp();

            }

            @Override
            public void onRight() {
                // TODO Auto-generated method stub
                Gdx.app.log(TAG,"MAgic - swiped right");
                playerOne.turnRight();

            }

            @Override
            public void onLeft() {
                // TODO Auto-generated method stub
                Gdx.app.log(TAG,"MAgic - swiped left");
                playerOne.turnLeft();

            }

            @Override
            public void onDown() {
                // TODO Auto-generated method stub
                Gdx.app.log(TAG,"MAgic - swiped down");
                playerOne.turnDown();

            }
        }));

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        cam.update();
        playerOne.update();
        //Gdx.app.log("LavaGame","xPos: " + player.getxPos() + " yPos: " + player.getyPos());

        // Check if one second has passed
        board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM-10)/48)).get((playerOne.getxPos()/48)).deteriorate();
        if (collector >= 1){
            Gdx.app.log("LavaGame","xPos: " + playerOne.getxPos() + " yPos: " + playerOne.getyPos() +" ----- ");
            Gdx.app.log("LavaGame","xPos: " + playerOne.getxPos()/48 + " yPos: " + (playerOne.getyPos()-CUTOFF_BOTTOM)/48 +" ----- ");
            Gdx.app.log("LavaGame"," size: "+board.getBoard().size());
            collector = 0;
            // Doo something every second
        } else {
            collector += dt;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);

        for (int r = 0; r < board.getBoard().size(); r++) {
            for (int c = 0; c < board.getBoard().get(r).size(); c++) {
                //Gdx.app.log("Tiles","x: " + board.getBoard().get(r).get(c).getxPos() + " y: " + board.getBoard().get(r).get(c).getyPos()+ " HL: " + board.getBoard().get(r).get(c).getHalfLife());
                sb.draw(board.getBoard().get(r).get(c).getTexture(),
                        board.getBoard().get(r).get(c).getxPos()* 48,
                        board.getBoard().get(r).get(c).getyPos()*48+CUTOFF_BOTTOM,
                        48,48);
            }
        }
        sb.draw(playerOne.getTexture(),playerOne.getxPos(),playerOne.getyPos(),16,16);
        sb.end();
    }

    @Override
    public void dispose() {

    }




}
