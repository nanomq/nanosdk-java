package io.sisu.nng.pubsub;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Pub0Socket extends Socket {
    public Pub0Socket() throws NngException {
        super(Nng.lib()::nng_pub0_open);
    }
}
