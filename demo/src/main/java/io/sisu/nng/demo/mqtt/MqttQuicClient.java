package io.sisu.nng.demo.mqtt;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;
import io.sisu.nng.mqtt.MqttQuicClientSocket;
import io.sisu.nng.mqtt.data.TopicQos;
import io.sisu.nng.mqtt.msg.ConnectMsg;
import io.sisu.nng.mqtt.msg.PublishMsg;
import io.sisu.nng.mqtt.msg.SubscribeMsg;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    private Message createConnMsg() throws NngException {
        ConnectMsg connMsg = new ConnectMsg();
        connMsg.setCleanSession(true);
        connMsg.setKeepAlive((short) 60);
        connMsg.setClientId("nng-java" + new Random());
        return connMsg;
    }

    public void conn() throws NngException {
        Message connMsg = createConnMsg();
        try (MqttQuicClientSocket sock = new MqttQuicClientSocket(this.url)) {
            System.out.println("Created mqtt quic socket");
            sock.setConnectCallback((rmsg, arg) ->
                            System.out.println(arg),
                    "Received Connack");
            sock.setDisconnectCallback((rmsg, arg) -> {
                System.out.println(arg);
            }, "Disconnected");

            sock.sendMessage(connMsg);
        }

    }

    public void sub() throws NngException {
        Message connMsg = createConnMsg();
        try (MqttQuicClientSocket sock = new MqttQuicClientSocket(this.url)) {
            System.out.println("Created mqtt quic socket");
            sock.setConnectCallback((msgPointer, arg) -> {
                        System.out.println(arg);
                        List<TopicQos> topicQosList = Arrays.asList(new TopicQos("topic1", (byte) 0), new TopicQos("topic2", (byte) 1), new TopicQos("topic3", (byte) 2));
                        SubscribeMsg subMsg = new SubscribeMsg(topicQosList);
                        sock.sendMessage(subMsg);
                    },
                    "Received Connack");
            sock.setDisconnectCallback(
                    (msgPointer, arg) ->
                            System.out.println(arg)
                    , "Disconnected");
            sock.setReceiveCallback((msgPointer, arg) -> {
                System.out.println(arg);
                PublishMsg recvMsg = new PublishMsg(msgPointer);
                System.out.printf("Topic: %s, Payload: %s, Qos: %d\n",
                        recvMsg.getTopic(),
                        recvMsg.getPayload(),
                        recvMsg.getQos());
            }, "Received message:");
            sock.sendMessage(connMsg);
        }
    }

    public void pub() throws NngException {
        Message connMsg = createConnMsg();
        try (MqttQuicClientSocket sock = new MqttQuicClientSocket(this.url)) {
            System.out.println("Created mqtt quic socket");
            sock.setConnectCallback((rmsg, arg) -> {
                        System.out.println(arg);
                        PublishMsg pubMsg = new PublishMsg();
                        pubMsg.setPayload(this.payload.getBytes(), this.payload.length());
                        pubMsg.setQos(this.qos);
                        pubMsg.setTopic(this.topic);
                        sock.sendMessage(pubMsg);
                    },
                    "Received Connack");
            sock.setSendCallback(
                    (recvPoint, arg) -> System.out.println(arg),
                    "Send Callback");
            sock.sendMessage(connMsg);
        }
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
            for (; ; ) {
                Thread.sleep(1000);
            }
        }
    }
}
