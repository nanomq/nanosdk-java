package io.sisu.nng;

import com.sun.jna.Native;
import io.sisu.nng.internal.NngLibrary;

/**
 * Simple singleton access point. Only works in simple environments where libnng is installed in standard places.
 */
public class Nng {
    private static NngLibrary library = null;

    public static NngLibrary lib() {
        if (library == null) {
            System.err.println("Loading libnng...");
            System.setProperty("jna.debug_load", "true");
            library = (NngLibrary) Native.load("nng", NngLibrary.class);
        }
        return library;
    }
}
