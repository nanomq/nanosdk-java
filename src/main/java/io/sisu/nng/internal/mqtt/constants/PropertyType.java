package io.sisu.nng.internal.mqtt.constants;

public enum PropertyType {
    U8(0),
    U16(1),
    U32(2),
    VARINT(3),
    BINARY(4),
    STR(5),
    STR_PAIR(6),
    UNKNOWN(7);

    private final int value;

    PropertyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
