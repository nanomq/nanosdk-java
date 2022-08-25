package io.sisu.nng.mqtt;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.NngMsgCallback;

public class MqttQuicClientSocket extends Socket {
    public MqttQuicClientSocket(String url) throws NngException {
        super(Nng.lib()::nng_mqtt_quic_client_open, url);
    }

    public void setConnectCallback(NngMsgCallback callback, String arg) {
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Nng.lib().nng_mqtt_quic_set_connect_cb(super.getSocketStruct(), callback, m);
    }

    public void setConnectCallback(NngMsgCallback callback, int arg) {
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Nng.lib().nng_mqtt_quic_set_connect_cb(super.getSocketStruct(), callback, m);
    }

    public void setDisconnectCallback(NngMsgCallback callback, String arg) {
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Nng.lib().nng_mqtt_quic_set_disconnect_cb(super.getSocketStruct(), callback, m);
    }

    public void setDisconnectCallback(NngMsgCallback callback, int arg) {
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Nng.lib().nng_mqtt_quic_set_disconnect_cb(super.getSocketStruct(), callback, m);
    }

    public void setReceiveCallback(NngMsgCallback callback, String arg) {
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Nng.lib().nng_mqtt_quic_set_msg_recv_cb(super.getSocketStruct(), callback, m);
    }

    public void setReceiveCallback(NngMsgCallback callback, int arg) {
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Nng.lib().nng_mqtt_quic_set_msg_recv_cb(super.getSocketStruct(), callback, m);
    }

    public void setSendCallback(NngMsgCallback callback, String arg) {
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Nng.lib().nng_mqtt_quic_set_msg_send_cb(super.getSocketStruct(), callback, m);
    }

    public void setSendCallback(NngMsgCallback callback, int arg) {
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Nng.lib().nng_mqtt_quic_set_msg_send_cb(super.getSocketStruct(), callback, m);
    }
}
