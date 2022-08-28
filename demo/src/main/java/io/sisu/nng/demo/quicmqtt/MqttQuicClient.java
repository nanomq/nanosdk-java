package io.sisu.nng.demo.quicmqtt;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;
import io.sisu.nng.mqtt.MqttQuicClientSocket;
import io.sisu.nng.mqtt.callback.QuicCallback;
import io.sisu.nng.mqtt.data.TopicQos;
import io.sisu.nng.mqtt.msg.ConnectMsg;
import io.sisu.nng.mqtt.msg.PublishMsg;
import io.sisu.nng.mqtt.msg.SubscribeMsg;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

public class MqttQuicClient {
    private final String url;

    public MqttPacketType packetType;
    private String topic;
    private byte qos;
    private String payload;

    private void setTopic(String topic) {
        this.topic = topic;
    }

    private void setQos(byte qos) {
        this.qos = qos;
    }

    public MqttQuicClient(String url) {
        this.url = url;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    private MqttQuicClientSocket sock;

    private final String disconnInfo = "Callback: Disconnected";
    private final String sendInfo = "Callback: Sent";


    private ConnectMsg createConnMsg() throws NngException {
        ConnectMsg connMsg = new ConnectMsg();
        connMsg.setCleanSession(true);
        connMsg.setKeepAlive((short) 60);
        connMsg.setClientId("nng-java" + UUID.randomUUID());
        connMsg.setProtoVersion(4);
        return connMsg;
    }

    final BiFunction<Message, Socket, Integer> connectHandler = (msg, sock) -> {
        String connInfo = "Callback: Connected";
        System.out.println(connInfo);
        try {
            if (this.packetType == MqttPacketType.NNG_MQTT_SUBSCRIBE) {
                List<TopicQos> topicQosList = Collections.singletonList(new TopicQos(this.topic, this.qos));
                SubscribeMsg subMsg = new SubscribeMsg(topicQosList);
                sock.sendMessage(subMsg);
            } else if (this.packetType == MqttPacketType.NNG_MQTT_PUBLISH) {
                PublishMsg pubMsg = new PublishMsg();
                pubMsg.setPayload(this.payload);
                pubMsg.setQos(this.qos);
                pubMsg.setTopic(this.topic);
                sock.sendMessage(pubMsg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return -1;
        }
        return 0;
    };

    final BiFunction<Message, String, Integer> handler = (msg, arg) -> {
        System.out.println(arg);
        return 0;
    };

    final BiFunction<Message, String, Integer> recvHandler = (msg, arg) -> {
        System.out.println(arg);
        try {
            PublishMsg publishMsg = new PublishMsg(msg);
            System.out.println("Topic: " + publishMsg.getTopic());
            System.out.println("Qos: " + publishMsg.getQos());
            System.out.println("Payload: " + StandardCharsets.UTF_8.decode(publishMsg.getPayload()));
        } catch (NngException e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }


        return 0;
    };

    public void conn() throws NngException, InterruptedException {
        ConnectMsg connMsg = createConnMsg();

        this.sock = new MqttQuicClientSocket(this.url);
        System.out.println("Created mqtt quic socket");
        this.sock.setConnectCallback(new QuicCallback(connectHandler), this.sock);
        this.sock.setDisconnectCallback(new QuicCallback(handler), disconnInfo);
        this.sock.sendMessage(connMsg);

        for (; ; ) {
            Thread.sleep(1000);
        }

    }

    public void sub() throws NngException, InterruptedException {
        ConnectMsg connMsg = createConnMsg();
        this.sock = new MqttQuicClientSocket(this.url);
        System.out.println("Created mqtt quic socket");
        this.sock.setConnectCallback(new QuicCallback(connectHandler), this.sock);
        this.sock.setDisconnectCallback(new QuicCallback(handler), disconnInfo);
        String receivedInfo = "Callback: Received";
        this.sock.setReceiveCallback(new QuicCallback(recvHandler), receivedInfo);
        this.sock.setSendCallback(new QuicCallback(handler), sendInfo);
        this.sock.sendMessage(connMsg);

        for (; ; ) {
            Thread.sleep(1000);
        }
    }

    public void pub() throws NngException, InterruptedException {
        ConnectMsg connMsg = createConnMsg();
        this.sock = new MqttQuicClientSocket(this.url);
        System.out.println("Created mqtt quic socket");
        this.sock.setConnectCallback(new QuicCallback(connectHandler), this.sock);
        this.sock.setDisconnectCallback(new QuicCallback(handler), disconnInfo);
        this.sock.setSendCallback(new QuicCallback(handler), sendInfo);
        this.sock.sendMessage(connMsg);
        Thread.sleep(500);
    }

    public static void main(String[] args) throws NngException, InterruptedException {
        if (args.length < 2) {
            System.err.println("Usage:");
            System.err.println("\tMqttQuicClient conn <url> ");
            System.err.println("\tMqttQuicClient pub  <url> <topic> <qos> <msg>");
            System.err.println("\tMqttQuicClient sub  <url> <topic> <qos>");
        } else {
            final String url = args[1];
            MqttQuicClient client = new MqttQuicClient(url);
            switch (args[0].toLowerCase()) {
                case "conn":
                    client.packetType = MqttPacketType.NNG_MQTT_CONNECT;
                    client.conn();
                    break;
                case "sub":
                    client.packetType = MqttPacketType.NNG_MQTT_SUBSCRIBE;
                    client.setTopic(args[2]);
                    client.setQos(Byte.parseByte(args[3]));
                    client.sub();
                    break;
                case "pub":
                    client.packetType = MqttPacketType.NNG_MQTT_PUBLISH;
                    client.setTopic(args[2]);
                    client.setQos(Byte.parseByte(args[3]));
                    client.setPayload(args[4]);
                    client.pub();
                    break;
                default:
                    throw new IllegalArgumentException("expected 'conn', 'sub' or 'pub', got " + args[0]);
            }

        }
    }
}
