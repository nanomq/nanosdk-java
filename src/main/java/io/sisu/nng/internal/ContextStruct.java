package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class ContextStruct extends Structure {
    public int id = 0;
    public ContextStruct() {}

    public static class ByValue extends ContextStruct implements Structure.ByValue {
        public ByValue() {}
        public ByValue(ContextStruct c) {
            this.id = c.id;
        }
    }
}
