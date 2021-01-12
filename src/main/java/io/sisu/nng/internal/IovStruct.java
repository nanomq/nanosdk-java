package io.sisu.nng.internal;

import com.sun.jna.Structure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Structure.FieldOrder({"iov_buf", "iov_len"})
public class IovStruct extends Structure {
    public ByteBuffer iov_buf;
    public int iov_len;

    public void reinitialize(int size) {
        iov_buf = ByteBuffer.allocateDirect(size);
        iov_buf.order(ByteOrder.nativeOrder());
        iov_buf.clear();
        iov_len = size;
    }

    public static IovStruct allocate(int size) {
        IovStruct iov = new IovStruct();
        iov.reinitialize(size);
        return iov;
    }

    public static IovStruct[] allocate(int ...sizes) {
        IovStruct[] array = (IovStruct[]) (new IovStruct()).toArray(sizes.length);
        for (int i=0; i< sizes.length; i++) {
            array[i].reinitialize(sizes[i]);
        }
        return array;
    }

    @Override
    public String toString() {
        return "IovStruct{" +
                "iov_buf=" + StandardCharsets.UTF_8.decode(iov_buf).toString() +
                ", iov_len=" + iov_len +
                '}';
    }
}
