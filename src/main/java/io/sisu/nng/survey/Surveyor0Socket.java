package io.sisu.nng.survey;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.NngOptions;

public class Surveyor0Socket extends Socket {
    public Surveyor0Socket() throws NngException {
        super(Nng.lib()::nng_surveyor0_open);
    }

    public void setSurveyDuration(int durationMillis) throws NngException {
        int rv = Nng.lib()
                .nng_socket_set_ms(this.socket, NngOptions.SURVEYOR_SURVEYTIME, durationMillis);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }
}
