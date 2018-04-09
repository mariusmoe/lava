package com.lava.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    // Remember that all pixel sizes are in "virtual" pixels stretched by the camera
    public static int X_TILES       = 10;   // Number of tiles in x direction
    public static int Y_TILES       = 12;   // Number of tiles in y direction
    public static int PLAYER_HEIGHT = 25;   //16;   // Height in pixels of the player
    public static int PLAYER_WIDTH  = 25;   //16;   // Width  in pixels of the player
    public static int CUTOFF_BOTTOM = 120;  //80;   // Bottom offset (get some space at the bottom)
    public static int TILE_SIZE     = 72;   //48;   // A tile is a square with that pany pixels
    public static String TAG        = "LavaGame";   // Use "lava" or "Lava" in the regex for debugging
    public static int BOARD_HEIGHT = (Y_TILES * TILE_SIZE) + CUTOFF_BOTTOM;
    private float INTERPOLATION_CONSTANT = (float) 5;    // How fast the interpolation will happen


    // Collectors are used for accumulating delta time, to enable actions that only execute after
    // a given time period has passed
    private float collector     = 0;    // collector used for deterioration of tiles
    private float tickCollector = 0;    // multiplayer tick collector
    private boolean toCancel = false;   // Because of rendering cycles "toCancel" is set to true when
                                        // the game is to be canceled at the next cycle. Or else
                                        // there will be rendering issues.
    private Player playerOne;


    private Player playerTwo;

    private Boolean multiplayer;
    private int serialNumber = 0;       // multiplayer packets can arrive out of order and are given
    private boolean canInterpolate = false;

    private int xTileToBeUpdated;
    private int yTileToBeUpdated;

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
            playerTwo = new Player(new Texture("player.png"));
            game.playServices.registerGameState(this);
        }

        // Let gdx handle swipes in a separate thread
        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(
                new SimpleDirectionGestureDetector.DirectionListener() {
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
        // Override back button presses to navigate to main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            game.playServices.leaveRoom();
            gsm.set(new MenuState(gsm, game));
            dispose();
        }
    }

    @Override
    public void update(float dt) {
        cam.update();   // Is this necessary?
        board.update(dt);
        playerOne.update(dt);
        if (multiplayer){
            playerTwo.update(dt);
            // Since texture changes should happen inside a "render loop" the tile that was damaged
            // by playerTwo gets updated in the next iteration -> here
            board.getBoard().get(yTileToBeUpdated).get(xTileToBeUpdated).update();
        }
        handleInput();

        // Canceling outside a "render loop" caused anomalies like black textures
        if (toCancel){
            gsm.set(new MenuState(gsm, game));
            dispose();
        }
        //Gdx.app.log("LavaGame","xPos: " + player.getxPos() + " yPos: " + player.getyPos());

        // Check if in lava => dead
        if (board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM)/TILE_SIZE))
                            .get((playerOne.getxPos()/TILE_SIZE)).getHp() <= 0 &&
                System.currentTimeMillis() -
                board.getBoard().get(((playerOne.getyPos()-CUTOFF_BOTTOM)/TILE_SIZE))
                        .get((playerOne.getxPos()/TILE_SIZE)).getTimeTileBecameLava()< 1000) {
            Gdx.app.log(TAG,"Player died! abort game now...");
            // TODO: Kill the player
            // TODO: NYI send death message if multiplayer!!!

        }

        if (collector >= 0.066f){
            board.getBoard().get((((playerOne.getyPos() - CUTOFF_BOTTOM) + (PLAYER_HEIGHT/2))/
                                  TILE_SIZE))
                            .get(((playerOne.getxPos() + (PLAYER_WIDTH/2))/TILE_SIZE))
                            .deteriorate();
            board.getBoard().get((((playerOne.getyPos() - CUTOFF_BOTTOM) + (PLAYER_HEIGHT/2))/
                                  TILE_SIZE))
                            .get(((playerOne.getxPos() + (PLAYER_WIDTH/2))/TILE_SIZE))
                            .update();
            // Todo: if multiplayer => send reliable message about damage to tile
            if (multiplayer){
                // send reliable damage message
                game.playServices.sendReliableMessage(reliableMessage());
            }
            collector = 0;
        } else {
            collector += dt;
        }

        if (multiplayer){
            if (canInterpolate){
                interpolate(dt, playerTwo.getxPos(), playerTwo.getyPos());
            }

            // 0.033 will trigger 30 times per second
            // 0.016 will trigger 60 times per second
            if (tickCollector >= 0.033f) {
                // Build byte array
                //Gdx.app.log(TAG," Building byte array: ");

                game.playServices.sendUnreliableMessage(unreliableMessage());
                //Gdx.app.log(TAG," Building byte array: " + Arrays.toString(message));
                serialNumber++;
                tickCollector = 0;
            } else {
                tickCollector += dt;
            }
        }
    }

    private byte[] unreliableMessage() {
        byte pos = (byte) 'P';
        byte[] serialNumberByte = intToByteArray(serialNumber);
        byte[] xPos = intToByteArray(playerOne.getxPos());
        byte[] yPos = intToByteArray(playerOne.getyPos());
        byte[] message = new byte[1 + serialNumberByte.length + xPos.length + yPos.length + 1];
        message[0] = pos;
        // TODO: clean up this code! use write()?
        for (int i = 0; i < message.length; ++i) {
            if (i < xPos.length) {
                message[i + 1] = serialNumberByte[i];
            }
            else if (i < (serialNumberByte.length + xPos.length)) {
                message[i + 1] = xPos[i - serialNumberByte.length];
            }
            else if (i < (xPos.length + yPos.length + serialNumberByte.length)) {
                message[i + 1] = yPos[i - xPos.length - yPos.length];
            }
            else if (i < (xPos.length + yPos.length + serialNumberByte.length + 1)) {
                message[i + 1] = (byte) playerOne.getDir();
            }
        }
        return message;
    }

    private byte[] reliableMessage() {
        byte messageType = (byte) 'D';
        byte[] xTile = intToByteArray(((playerOne.getxPos() + (PLAYER_WIDTH/2))/TILE_SIZE));
        byte[] yTile = intToByteArray((((playerOne.getyPos() - CUTOFF_BOTTOM) + (PLAYER_HEIGHT/2))/
                                       TILE_SIZE));
        byte[] reliableMessage = new byte[1 + xTile.length + yTile.length];
        reliableMessage[0] = messageType;
        for (int i = 0; i < reliableMessage.length; ++i) {
            if (i < xTile.length) {
                reliableMessage[i + 1] = xTile[i];
            }
            else if (i < (xTile.length + yTile.length)) {
                reliableMessage[i + 1] = yTile[i - xTile.length];
            }
        }
        return reliableMessage;
    }

    public void receivePosition(int receivedXPos, int receivedYPos, int dir){
        // https://stackoverflow.com/questions/3276821/dealing-with-lag-in-xna-lidgren/3276994#3276994
        //Gdx.app.log(TAG," received pos: " + receivedXPos + " : "+ receivedYPos);
        playerTwo.setReceivedPos(receivedXPos, receivedYPos, dir);
        canInterpolate = true;
    }

    /**
     * Received damage to a tile in multiplayer
     * @param tileX     Defines x coordinate for tile
     * @param tileY     Defines Y coordinate for tile
     */
    public void receiveDamageToTile(int tileX, int tileY) {
        Gdx.app.log(TAG," Try to deteriorate tile nr: " + tileY + " : " + tileX );
        board.getBoard().get(tileY).get(tileX).deteriorate();
        xTileToBeUpdated = tileX;
        yTileToBeUpdated = tileY;
    }

    /**
     * Interpolate the new position of playerTwo to smooth movement in multiplayer
     * @param dt
     * @param xPos  Received X position from player two
     * @param yPos  Received Y position from player two
     */
    private void interpolate(float dt, int xPos, int yPos) {
        if (playerTwo.getReceivedXPos() != 0 && playerTwo.getReceivedYPos() != 0){
            if ((Math.abs(playerTwo.getxPos() - playerTwo.getReceivedXPos()) > 25) ||
                (Math.abs(playerTwo.getyPos() - playerTwo.getReceivedYPos()) > 25)){
                Gdx.app.log(TAG," Interpolating " );

                    // TODO: Improve interpolation...
                    // maybe increase progress when player is close to a wall?
                    // current progress value is kind of a random number
                    float progress;
                if (dt * 5f > 0.999f){
                        progress = 0.06f;
                    } else {
                    progress = dt * 4f;
                    }
                    playerTwo.setPos(Math.round((MathUtils.lerp(xPos,playerTwo.getReceivedXPos(),
                                                                progress))),
                                     Math.round((MathUtils.lerp(yPos,playerTwo.getReceivedYPos(),
                                                                progress))));
                }
            }
    }

    public void cancelGame() {
        toCancel = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setProjectionMatrix(cam.combined);

        sb.draw(board.getBackground(), board.getBoard().get(0).get(0).getxPos() * TILE_SIZE, board.getBoard().get(0).get(0).getyPos() * TILE_SIZE + CUTOFF_BOTTOM, X_TILES * TILE_SIZE, Y_TILES * TILE_SIZE);
        for (int r = 0; r < board.getBoard().size(); r++) {
            for (int c = 0; c < board.getBoard().get(r).size(); c++) {
                sb.draw(board.getBoard().get(r).get(c).getTexture(),
                        board.getBoard().get(r).get(c).getxPos() * TILE_SIZE,
                        board.getBoard().get(r).get(c).getyPos() * TILE_SIZE + CUTOFF_BOTTOM,
                        TILE_SIZE, TILE_SIZE);
            }
        }
        sb.draw(playerOne.getTexture(), playerOne.getxPos(), playerOne.getyPos(),
                PLAYER_HEIGHT, PLAYER_HEIGHT);
        if (multiplayer) {
            sb.draw(playerTwo.getTexture(), playerTwo.getxPos(), playerTwo.getyPos(),
                    PLAYER_HEIGHT, PLAYER_HEIGHT);
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
