package io.sisu.nng;

/**
 * Wrapper around nng error messages.
 *
 * TODO: provide ability to easily distinguish between common error types (e.g. timeout).
 */
public class NngException extends Exception {

    public NngException(String message) {
        super(message);
    }
}
