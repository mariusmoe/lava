package com.lava.game;

import com.lava.game.states.MenuState;
import com.lava.game.states.PlayState;

/**
 * Created by moe on 08.03.18.
 */

public interface PlayServices {
    public void signIn();
    public void signOut();
    public boolean isSignedIn();
    public void startQuickGame(MenuState mState);
    public void registerGameState(PlayState pstate);
    public void sendReliableMessage(byte[] message);

    /**
     * Send a unreliable message to all participants in the current game session
     * @param message	A byte array consisting of the following:
     *                  0: 		The letters P or D for Position or Damage, respectively encoded as a
     *                 			byte
     *                  1-5:	Integer serial number encoded as four bytes
     *                  5-9:	Integer xPos encoded as four bytes
     *                  9-13:	Integer yPos encoded as for bytes
     */
    public void sendUnreliableMessage(byte[] message);
}
