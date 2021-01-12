package io.sisu.nng.pubsub;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.NngOptions;

public class Sub0Socket extends Socket {

    public Sub0Socket() throws NngException {
        super(Nng.lib()::nng_sub0_open);
    }

    public void subscribe(String topic) throws NngException {
        int rv = Nng.lib().nng_socket_set_string(this.socket, NngOptions.SUBSCRIBE, topic);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void unsubscribe(String topic) throws NngException {
        int rv = Nng.lib().nng_socket_set_string(this.socket, NngOptions.UNSUBSCRIBE, topic);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

}
