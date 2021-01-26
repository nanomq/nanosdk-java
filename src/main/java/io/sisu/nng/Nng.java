package io.sisu.nng;

import com.sun.jna.Native;
import io.sisu.nng.internal.NngLibrary;

import java.util.Properties;

/**
 * Simple singleton access point. Only works in simple environments where libnng is installed in
 * standard places.
 *
 * Use either the <pre>jna.library.path</pre> System property or set a <pre>JNA_LIBRARY_PATH</pre>
 * environment variable to point to the location of libnng if it's not already accessible. You may
 * need to also add this to the classpath.
 */
public class Nng {
    private static NngLibrary library = null;

    public static NngLibrary lib() {
        if (library == null) {
            final Properties props = System.getProperties();
            if (!props.contains("jna.debug_load")) {
                System.setProperty("jna.debug_load",
                        System.getenv().getOrDefault("JNA_DEBUG_LOAD", ""));
            }
            if (!props.contains("jna.library.path")) {
                System.setProperty("jna.library.path",
                        System.getenv().getOrDefault("JNA_LIBRARY_PATH", ""));
            }

            library = Native.load("nng", NngLibrary.class);
        }
        return library;
    }
}
