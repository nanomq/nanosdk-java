package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class PipeStruct extends Structure {
    public int id;

    public static class ByValue extends PipeStruct implements Structure.ByValue {
        public ByValue(PipeStruct p) {
            this.id = p.id;
        }
    }
}
