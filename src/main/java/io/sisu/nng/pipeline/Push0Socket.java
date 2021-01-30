package io.sisu.nng.pipeline;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Push0Socket extends Socket {
    public Push0Socket() throws NngException {
        super(Nng.lib()::nng_push0_open);
    }
}
