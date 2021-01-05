package io.sisu.nng.reqrep0;

import io.sisu.nng.Message;
import io.sisu.nng.Socket;
import io.sisu.nng.Nng;
import io.sisu.nng.internal.SocketStruct;

public class Rep0Socket extends Socket {
    private final SocketStruct sock;

    public Rep0Socket() throws Exception {
        sock = new SocketStruct();
        int rv = Nng.lib().nng_rep0_open(sock);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new Exception(err);
        }
    }

    @Override
    public void sendMessage(Message msg) {

    }

    @Override
    public Message recvMessage() {
        return null;
    }
}
