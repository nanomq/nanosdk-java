package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class DialerStruct extends Structure {
    public int id = 0;

    public DialerStruct() {}

    public static class ByValue extends DialerStruct implements Structure.ByValue {
        public ByValue() {}
        public ByValue(DialerStruct d) {
            this.id = d.id;
        }
    }
}
