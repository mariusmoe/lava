package com.lava.game;

import com.lava.game.states.MenuState;
import com.lava.game.states.PlayState;

/**
 * Created by moe on 08.03.18.
 */

public interface PlayServices {


    /**
     * Sign in manually
     * This wil send an intent to GPGS for login
     */
    public void signIn();


    @Deprecated
    public void signOut();


    @Deprecated
    public boolean isSignedIn();

    /**
     * Leave the current room
     */
    public void leaveRoom();    // TODO: Add callback


    /**
     * Imitate a quick game session
     * @param mState    Provide the menu state so that it can be called back when the room is full
     */
    public void startQuickGame(MenuState mState);


    /**
     * Has to be called before multiplayer messages can be used by the game
     * @param pstate
     */
    public void registerGameState(PlayState pstate);


    /**
     * Send a reliable message to all participants in the current game session
     * @param message   A byte array consisting of the following:
     *                  <ul>
     *                      <li>0: The letters P or D for Position or Damage, respectively encoded
     *                          as a byte, for reliable messages this should be 'D'
     *                      </li>
     *                      <li>1-5: Integer x coordinate for tile encoded as four bytes</li>
     *                      <li>5-9: Integer Y coordinate for tile encoded as four bytes</li>
     *                  </ul>
     */
    public void sendReliableMessage(byte[] message);


    /**
     * Send a unreliable message to all participants in the current game session
     * @param message	A byte array consisting of the following:
     *                  <ul>
     *                      <li>0: The letters P or D for Position or Damage, respectively encoded
     *                          as a byte, for unreliable messages this should be 'P'
     *                      </li>
     *                      <li>1-5: Integer serial number encoded as four bytes</li>
     *                      <li>5-9: Integer xPos encoded as four bytes</li>
     *                      <li>9-13:	Integer yPos encoded as for bytes</li>
     *                  </ul>
     */
    public void sendUnreliableMessage(byte[] message);
}
