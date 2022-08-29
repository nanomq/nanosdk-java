package io.sisu.nng.mqtt;

import com.sun.jna.*;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.mqtt.callback.QuicCallback;

public class MqttQuicClientSocket extends Socket {
    public MqttQuicClientSocket(String url) throws NngException {
        super(Nng.lib()::nng_mqtt_quic_client_open, url);
    }

    public void setConnectCallback(QuicCallback callback, String arg) {
        callback.setArg(arg);
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "ConnectCallback"));
        Nng.lib().nng_mqtt_quic_set_connect_cb(super.getSocketStruct(), callback, m);
    }

    public void setConnectCallback(QuicCallback callback, int arg) {
        callback.setArg(arg);
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "ConnectCallback"));
        Nng.lib().nng_mqtt_quic_set_connect_cb(super.getSocketStruct(), callback, m);
    }

    public void setConnectCallback(QuicCallback callback, Socket arg) {
        callback.setArg(arg);
        Pointer p = arg.getSocketStruct().getPointer();
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "ConnectCallback"));
        Nng.lib().nng_mqtt_quic_set_connect_cb(super.getSocketStruct(), callback, p);
    }

    public void setDisconnectCallback(QuicCallback callback, String arg) {
        callback.setArg(arg);
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "DisconnectCallback"));
        Nng.lib().nng_mqtt_quic_set_disconnect_cb(super.getSocketStruct(), callback, m);
    }

    public void setDisconnectCallback(QuicCallback callback, int arg) {
        callback.setArg(arg);
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "DisconnectCallback"));
        Nng.lib().nng_mqtt_quic_set_disconnect_cb(super.getSocketStruct(), callback, m);
    }

    public void setReceiveCallback(QuicCallback callback, String arg) {
        callback.setArg(arg);
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "ReceiveCallback"));
        Nng.lib().nng_mqtt_quic_set_msg_recv_cb(super.getSocketStruct(), callback, m);
    }

    public void setReceiveCallback(QuicCallback callback, int arg) {
        callback.setArg(arg);
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "ReceiveCallback"));
        Nng.lib().nng_mqtt_quic_set_msg_recv_cb(super.getSocketStruct(), callback, m);
    }

    public void setSendCallback(QuicCallback callback, String arg) {
        callback.setArg(arg);
        Pointer m = new Memory(arg.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "SendCallback"));
        Nng.lib().nng_mqtt_quic_set_msg_send_cb(super.getSocketStruct(), callback, m);
    }

    public void setSendCallback(QuicCallback callback, int arg) {
        callback.setArg(arg);
        Pointer m = new Memory(4);
        m.setInt(0, arg);
        Native.setCallbackThreadInitializer(callback, new CallbackThreadInitializer(true, false, "SendCallback"));
        Nng.lib().nng_mqtt_quic_set_msg_send_cb(super.getSocketStruct(), callback, m);
    }
}
