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

    // TLS Options
    public static final String TLS_CONFIG = "tls-config";
    public static final String TLS_AUTH_MODE = "tls-authmode";
    public static final String TLS_CERT_KEY_FILE = "tls-cert-key-file";
    public static final String TLS_CA_FILE = "tls-ca-file";
    public static final String TLS_SERVER_NAME = "tls-server-name";
    public static final String TLS_IS_VERIFIED = "tls-verified";

    // WebSocket Options
    public static final String WS_REQUEST_HEADERS = "ws:request-headers";
    public static final String WS_RESPONSE_HEADERS = "ws:response-headers";
    public static final String WS_REQUEST_HEADER = "ws:request-header:";
    public static final String WS_RESPONSE_HEADER = "ws:response-header:";
    public static final String WS_REQUEST_URI = "ws:request-uri";
    public static final String WS_SEND_MAX_FRAME = "ws:txframe-max";
    public static final String WS_RECV_MAX_FRAME = "ws:rxframe-max";
    public static final String WS_PROTOCOL = "ws:protocol";
    public static final String WS_SEND_TEXT = "ws:send-text";
    public static final String WS_RECV_TEXT = "ws:recv-text";

    // PubSub Options
    public static final String SUBSCRIBE = "sub:subscribe";
    public static final String UNSUBSCRIBE = "sub:unsubscribe";
    public static final String PREFER_NEW_ON_FULL = "sub:prefnew";

    // Surveyor Options
    public static final String SURVEYOR_SURVEYTIME = "surveyor:survey-time";
}
