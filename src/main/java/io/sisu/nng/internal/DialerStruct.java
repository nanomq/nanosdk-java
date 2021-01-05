package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class DialerStruct extends Structure {
    public int id;

    public static class ByValue extends DialerStruct implements Structure.ByValue {
        public ByValue(DialerStruct d) {
            this.id = d.id;
        }
    }
}
