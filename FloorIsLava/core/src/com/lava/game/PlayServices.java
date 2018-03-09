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
    public void sendUnreliableMessage(byte[] message);
}
