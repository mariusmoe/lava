package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import java.util.Arrays;

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

    // Collectors are used for accumulating delta time, to enable actions that only execute after
    // a given time period has passed
    private float collector = 0;        // collector used for deterioration of tiles
    private float tickCollector = 0;    // multiplayer tick collector

    private Player playerOne;
    private Player playerTwo;
    private int threshold = 20;
    private float interpolation_constant = 0.5f;    // How fast the interpolation will happen

    Boolean multiplayer;
    private int serialNumber = 0;       // multiplayer packets can arrive out of order and are given
                                        // a serial number

    /**
     * Construct a new play state
     * @param gsm   game state manage
     * @param game  reference to the floor is lava (to reach multiplayer methods)
     * @param multiplayer   true if multiplayer, false if singleplayer
     */
    protected PlayState(GameStateManager gsm, FloorIsLava game, Boolean multiplayer) {
        super(gsm);
        this.game = game;
        this.multiplayer = multiplayer;
        this.board = new Board(X_TILES ,Y_TILES);

        playerOne = new Player(new Texture("pl.png"));
        if (multiplayer){
            playerTwo = new Player(new Texture("pl.png"));
            game.playServices.registerGameState(this);
        }

        // Let gdx handle swipes in a separate thread
        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

            @Override
            public void onUp() {
                Gdx.app.log(TAG,"MAgic - swiped up");
                playerOne.turnUp();
            }

            @Override
            public void onRight() {
                Gdx.app.log(TAG,"MAgic - swiped right");
                playerOne.turnRight();
            }

            @Override
            public void onLeft() {
                Gdx.app.log(TAG,"MAgic - swiped left");
                playerOne.turnLeft();
            }

            @Override
            public void onDown() {
                Gdx.app.log(TAG,"MAgic - swiped down");
                playerOne.turnDown();
            }
        }));

    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            gsm.set(new MenuState(gsm, game));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        cam.update();   // Is this necessary?
        playerOne.update();
        handleInput();
        //Gdx.app.log("LavaGame","xPos: " + player.getxPos() + " yPos: " + player.getyPos());

        // Check if in lava => dead
        if (board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM-10)/48)).get((playerOne.getxPos()/48)).getHp() == 0) {
            Gdx.app.log(TAG,"Player died! abort game now...");
            // TODO: Kill the player
            // TODO: send death message if multiplayer

        }

        board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM)/48)).get((playerOne.getxPos()/48)).deteriorate();
        // TODO: Decrease time for deterioration of tile
        //       This has to be small enough to be able to send it reliably in multiplayer, but also
        //       fas enough for the user to understand that walking over a tile deteriorate it
        if (collector >= 1){
            // Doo something every second
            Gdx.app.log(TAG,"xPos: " + playerOne.getxPos() + " yPos: " + playerOne.getyPos() +" ----- ");
            Gdx.app.log(TAG,"xPos: " + playerOne.getxPos()/48 + " yPos: " + (playerOne.getyPos()-CUTOFF_BOTTOM)/48 +" ----- ");
            Gdx.app.log(TAG," size: "+board.getBoard().size());

            board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM-10)/48))
                            .get((playerOne.getxPos()/48))
                            .deteriorate();

            // Todo: if multiplayer => send reliable message about damage to tile
            if (multiplayer){
                // send reliable damage message

            }
            collector = 0;
        } else {
            collector += dt;
        }

        // Multiplayer stuff here
        if (multiplayer){
            if (tickCollector >= 0.33) {
                // TODO: broadcast position
                // Build byte array
                byte    pos  = (byte) 'P';
                byte[]  serialNumberByte = intToByteArray(serialNumber);
                byte[]  xPos = intToByteArray(playerOne.getxPos());
                byte[]  yPos = intToByteArray(playerOne.getyPos());
                byte[] message = new byte[1 + serialNumberByte.length + xPos.length + yPos.length];
                message[0] = pos;
                // TODO: clean up this code!
                for (int i = 1; i < message.length; ++i) {
                    if (i < xPos.length) {
                        message[i] = xPos[i];
                    }
                    else {
                        if (i < (xPos.length + yPos.length)){
                        message[i] = yPos[i - xPos.length];
                        } else {
                            message[i] = yPos[i - xPos.length - yPos.length];
                        }
                    }
                }
                serialNumber++;
                game.playServices.sendUnreliableMessage(message);
            } else {
                tickCollector += dt;
            }
        }
    }

    public void receivePosition(int xPos, int yPos){
        // https://stackoverflow.com/questions/3276821/dealing-with-lag-in-xna-lidgren/3276994#3276994

        playerTwo.setPos(xPos, yPos);
    }

    /**
     * Received damage to a tile in multiplayer
     * @param tileX
     * @param tileY
     */
    public void receiveDamageToTile(int tileX, int tileY) {
        Gdx.app.log(TAG," Try to deteriorate tile nr: " + tileY + " : " + tileX );
        board.getBoard().get(tileY).get(tileX).deteriorate();
    }

    /**
     * Interpolate the new position of playerTwo to smooth movement in multiplayer
     * @param dt
     * @param xPos  Received X position from player two
     * @param yPos  Received Y position from player two
     */
    void Interpolate(float dt, int xPos, int yPos) {
        int differenceX = xPos - playerTwo.getxPos();
        int differenceY = xPos - playerTwo.getyPos();
        if (differenceX < threshold && differenceY < threshold)
            playerTwo.setPos(xPos, yPos);
        else
            playerTwo.setPos(Math.round(playerTwo.getxPos() + (differenceX * dt * interpolation_constant)),
                             Math.round(playerTwo.getyPos() + (differenceY * dt * interpolation_constant)));
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
        if (multiplayer) {
            sb.draw(playerTwo.getTexture(),playerTwo.getxPos(),playerTwo.getyPos(),16,16);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        board.dispose();
        playerOne.dispose();
    }

    // TODO - put this inside a utils helper class
    public static byte[] intToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    @Deprecated
    public static int byteArrayToInt(byte[] b) {
        return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
    }




}
