package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class LibraryTest {

    @Test
    public void TestDial() throws Exception {
        NngLibrary lib = Nng.lib();

        System.out.println("Loaded libnng");
        SocketStruct sockPtr = new SocketStruct();
        int rv = lib.nng_req0_open(sockPtr);
        System.out.println("open rv: " + rv);

        SocketStruct.ByValue sock = new SocketStruct.ByValue(sockPtr);
        System.out.println("sock id: " + sock.id);

        rv = lib.nng_dial(sock, "ipc:///tmp/nng", Pointer.NULL, 0);
        System.out.println("rv: " + rv);
        if (rv != 0) {
            String err = lib.nng_strerror(rv);
            System.err.println(err);
            Assertions.fail();
        }

        String raw = "hey man\n";
        ByteBuffer buffer = ByteBuffer.wrap(raw.getBytes(StandardCharsets.UTF_8));

        MessagePointer msg = MessagePointer.alloc(0, lib);

        int len = lib.nng_msg_len(msg);
        System.out.println("msg len: " + len);

        rv = lib.nng_msg_append(msg, buffer, buffer.limit());
        System.out.println("insert msg tv: " + rv);

        BodyPointer body = lib.nng_msg_body(msg);

        System.out.println("msg body: " + Charset.defaultCharset().decode(
                body.getPointer().getByteBuffer(0, len)
        ));

        rv = lib.nng_sendmsg(sock, msg, 0);
        System.out.println("sendmsg rv: " + rv);

        rv = lib.nng_msg_free(msg);
        System.out.println("msg free: " + rv);

    }
}
