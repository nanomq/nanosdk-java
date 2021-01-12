package io.sisu.nng.survey;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Respondent0 extends Socket {
    public Respondent0() throws NngException {
        super(Nng.lib()::nng_respondent0_open);
    }
}
