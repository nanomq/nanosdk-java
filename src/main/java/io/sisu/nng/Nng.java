package io.sisu.nng;

import com.sun.jna.Native;
import io.sisu.nng.internal.NngLibrary;

import java.util.Properties;

/**
 * Simple singleton access point. Only works in simple environments where libnng is installed in standard places.
 */
public class Nng {
    private static NngLibrary library = null;

    public static NngLibrary lib() {
        if (library == null) {
            System.err.println("Loading libnng...");

            final Properties props = System.getProperties();
            if (!props.contains("jna.debug_load")) {
                System.setProperty("jna.debug_load",
                        System.getenv().getOrDefault("JNA_DEBUG_LOAD", ""));
            }
            if (!props.contains("jna.library.path")) {
                System.setProperty("jna.library.path",
                        System.getenv().getOrDefault("JNA_LIBRARY_PATH", ""));
            }

            library = (NngLibrary) Native.load("nng", NngLibrary.class);
        }
        return library;
    }
}
