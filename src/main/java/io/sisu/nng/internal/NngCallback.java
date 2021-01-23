package io.sisu.nng.internal;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface NngCallback extends Callback {
    void callback(Pointer p);
}
