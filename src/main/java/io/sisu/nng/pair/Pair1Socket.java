package io.sisu.nng.pair;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Pair1Socket extends Socket {
    public Pair1Socket() throws NngException {
        super(Nng.lib()::nng_pair1_open);
    }
}
