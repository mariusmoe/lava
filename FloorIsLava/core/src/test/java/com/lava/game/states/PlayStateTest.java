package com.lava.game.states;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by moe on 22.03.18.
 */

public class PlayStateTest {
    @Test
    public void intToByteArray() throws Exception {
        byte[] aa = {(byte) 0, (byte) 0, (byte) 0, (byte) 1};
        byte[] bb = {(byte) 0, (byte) 0, (byte) 0, (byte) 2};
        assertEquals(Arrays.toString(PlayState.intToByteArray(1)), Arrays.toString(aa));
        assertNotEquals(Arrays.toString(PlayState.intToByteArray(1)), Arrays.toString(bb));
    }

    @Test
    public void byteArrayToInt() throws Exception {
    }

}