package io.sisu.nng.survey;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Surveyor0Socket extends Socket {
    public Surveyor0Socket() throws NngException {
        super(Nng.lib()::nng_surveyor0_open);
    }
}
