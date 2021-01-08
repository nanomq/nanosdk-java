package io.sisu.nng.reqrep0;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Pair0Socket extends Socket {
    public Pair0Socket() throws NngException {
        super(Nng.lib()::nng_pair0_open);
    }
}
