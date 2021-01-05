package io.sisu.nng;

public abstract class Socket {
    private int id;
    public abstract void sendMessage(Message msg);
    public abstract Message recvMessage();
}
