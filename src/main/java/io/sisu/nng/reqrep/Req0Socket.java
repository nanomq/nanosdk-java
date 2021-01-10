package io.sisu.nng.reqrep;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Req0Socket extends Socket {

    public Req0Socket() throws NngException {
        super(Nng.lib()::nng_req0_open);
    }
}
