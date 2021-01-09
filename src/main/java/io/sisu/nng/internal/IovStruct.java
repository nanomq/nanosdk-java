package io.sisu.nng.internal;

import com.sun.jna.*;

@Structure.FieldOrder({"iov_buf", "iov_len"})
public class IovStruct extends PointerType {
    public Memory iov_buf;
    public short iov_len;
}
