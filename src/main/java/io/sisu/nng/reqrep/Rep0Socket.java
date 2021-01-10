package io.sisu.nng.reqrep;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Rep0Socket extends Socket {

    public Rep0Socket() throws NngException {
        super(Nng.lib()::nng_rep0_open);
    }
}
