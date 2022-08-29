package io.sisu.nng.internal.mqtt.constants;

public enum MqttPacketType {
    NNG_MQTT_CONNECT(0x01),
    NNG_MQTT_CONNACK(0x02),
    NNG_MQTT_PUBLISH(0x03),
    NNG_MQTT_PUBACK(0x04),
    NNG_MQTT_PUBREC(0x05),
    NNG_MQTT_PUBREL(0x06),
    NNG_MQTT_PUBCOMP(0x07),
    NNG_MQTT_SUBSCRIBE(0x08),
    NNG_MQTT_SUBACK(0x09),
    NNG_MQTT_UNSUBSCRIBE(0x0A),
    NNG_MQTT_UNSUBACK(0x0B),
    NNG_MQTT_PINGREQ(0x0C),
    NNG_MQTT_PINGRESP(0x0D),
    NNG_MQTT_DISCONNECT(0x0E),
    NNG_MQTT_AUTH(0x0F);

    private final byte value;

    MqttPacketType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static MqttPacketType getFromValue(byte value) {
        for (MqttPacketType packetType :
                MqttPacketType.values()) {
            if (packetType.getValue() == value) {
                return packetType;
            }
        }
        return null;
    }

}
