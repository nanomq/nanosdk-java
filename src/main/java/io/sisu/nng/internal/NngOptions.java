package io.sisu.nng.internal;

public class NngOptions {
    // General Pipe/Dialer/Listener Options
    public static final String SOCKET_NAME = "socket-name";
    public static final String RAW = "raw";
    public static final String PROTOCOL = "protocol";
    public static final String PROTOCOL_NAME = "protocol-name";
    public static final String PEER = "peer";
    public static final String PEER_NAME = "peer-name";
    public static final String RECV_BUF = "recv-buffer";
    public static final String SEND_BUF = "send-buffer";
    public static final String RECV_FD = "recv-fd";
    public static final String SEND_FD = "send-fd";
    public static final String RECV_TIMEOUT = "recv-timeout";
    public static final String SEND_TIMEOUT = "send-timeout";
    public static final String LOCAL_ADDR = "local-address";
    public static final String REMOTE_ADDR = "remote-address";
    public static final String URL = "url";
    public static final String MAX_TTL = "ttl-max";
    public static final String RECV_SIZE_MAX = "recv-size-max";
    public static final String RECONNECT_TIME_MIN = "reconnect-time-min";
    public static final String RECONNECT_TIME_MAX = "reconnect-time-max";

    // PubSub Options
    public static final String SUBSCRIBE = "sub:subscribe";
    public static final String UNSUBSCRIBE = "sub:unsubscribe";
    public static final String PREFER_NEW_ON_FULL = "sub:prefnew";

    // Surveyor Options
    public static final String SURVEYOR_SURVEYTIME = "surveyor:survey-time";
}
