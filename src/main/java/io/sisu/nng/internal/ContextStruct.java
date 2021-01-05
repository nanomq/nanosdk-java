package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class ContextStruct extends Structure {
    public int id;

    public static class ByValue extends ContextStruct implements Structure.ByValue {
        public ByValue(ContextStruct c) {
            this.id = c.id;
        }
    }
}
