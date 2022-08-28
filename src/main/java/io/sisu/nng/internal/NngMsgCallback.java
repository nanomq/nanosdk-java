package io.sisu.nng.internal;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import io.sisu.nng.NngException;

public interface NngMsgCallback extends Callback {
    int callback(Pointer p1, Pointer p2);
}
