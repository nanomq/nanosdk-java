package io.sisu.nng.internal;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface NngMsgCallback extends Callback {
    void callback(Pointer arg1, Pointer arg2);
}
