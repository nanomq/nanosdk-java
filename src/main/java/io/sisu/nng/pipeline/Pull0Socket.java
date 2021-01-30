package io.sisu.nng.pipeline;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Pull0Socket extends Socket {
    public Pull0Socket() throws NngException {
        super(Nng.lib()::nng_pull0_open);
    }
}
