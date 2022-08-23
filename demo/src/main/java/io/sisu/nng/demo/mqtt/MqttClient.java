package io.sisu.nng.demo.mqtt;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.mqtt.constants.MqttVersion;
import io.sisu.nng.mqtt.Mqtt5ClientSocket;
import io.sisu.nng.mqtt.MqttClientSocket;
import io.sisu.nng.mqtt.msg.ConnectMsg;
import io.sisu.nng.mqtt.msg.MqttMessage;

import java.util.Random;

public class MqttClient {
    private final String url;
    private final int version;

    public MqttClient(String url) {
        this.url = url;
        this.version = MqttVersion.MQTT_V311;
    }


    public MqttClient(String url, int version) {
        this.url = url;
        this.version = version;
    }

    public void run() throws NngException {
        ConnectMsg connMsg = new ConnectMsg();
        connMsg.setCleanSession(true);
        connMsg.setKeepAlive((short) 60);
        connMsg.setClientId("nng-java"+ new Random());
        connMsg.setProtoVersion(this.version);

        Socket sock;

        if(this.version == MqttVersion.MQTT_V5) {
            sock = new Mqtt5ClientSocket();
        }else {
            sock = new MqttClientSocket();
        }


        sock.dial(this.url);



    }
}
