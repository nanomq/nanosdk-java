package io.sisu.nng;

import com.sun.jna.Callback;
import io.sisu.nng.internal.NngDirectLibrary;
import io.sisu.nng.internal.NngLibrary;

import java.util.Properties;

/**
 * Simple singleton access point. Only works in simple environments where nng is installed in
 * standard places.
 * <p>
 * Use either the <pre>jna.library.path</pre> System property or set a <pre>JNA_LIBRARY_PATH</pre>
 * environment variable to point to the location of libnng if it's not already accessible. You may
 * need to also add this to the classpath.
 */
public class Nng {
    private static NngLibrary directLibrary = null;

    public static class NngUncaughtExceptionHandler implements Callback.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Callback c, Throwable e) {
            System.err.println("AIOCallback had an unhandled exception. This should not happen!");
            e.printStackTrace(System.err);
        }
    }

    public static final NngUncaughtExceptionHandler exceptionHandler = new NngUncaughtExceptionHandler();

    /**
     * Get a reference to the nng library, registering it if needed.
     *
     * @return a direct-mapped NngLibrary utilizing native methods
     */
    public static NngLibrary lib() {
        if (directLibrary == null) {
            checkEnvironmentConfig();
            directLibrary = new NngDirectLibrary();
        }
        return directLibrary;
    }

    /**
     * Copy environment settings for JNA into the Java system properties if they don't exist.
     */
    private static void checkEnvironmentConfig() {
        final Properties props = System.getProperties();
        if (!props.contains("jna.debug_load")) {
            System.setProperty("jna.debug_load",
                    System.getenv().getOrDefault("JNA_DEBUG_LOAD", ""));
        }
        if (!props.contains("jna.library.path")) {
            System.setProperty("jna.library.path",
                    System.getenv().getOrDefault("JNA_LIBRARY_PATH", ""));
        }
    }
}
