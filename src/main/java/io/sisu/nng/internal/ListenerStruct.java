package io.sisu.nng.internal;

import com.sun.jna.Structure;

@Structure.FieldOrder({ "id" })
public class ListenerStruct extends Structure {
    public int id = 0;

    public ListenerStruct() { }

    public static class ByValue extends ListenerStruct implements Structure.ByValue {
        public ByValue() {}
        public ByValue(ListenerStruct l) {
            this.id = l.id;
        }
    }
}
