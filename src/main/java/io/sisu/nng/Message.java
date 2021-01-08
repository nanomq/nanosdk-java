package io.sisu.nng;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.sisu.nng.internal.BodyPointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.MessagePointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Message {
    protected boolean valid = true;
    private MessagePointer msg;

    public Message() throws NngException {
        this(0);
    }

    public Message(int size) throws NngException {
        final MessageByReference ref = new MessageByReference();
        int rv = Nng.lib().nng_msg_alloc(ref, size);
        if (rv != 0) {
            final String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        msg = ref.getMessage();
    }

    protected Message(MessagePointer pointer) {
        this.msg = pointer;
    }

    public MessagePointer getMessagePointer() {
        return msg;
    }

    public boolean isValid() {
        return valid;
    }

    public void append(byte[] data) {
        int rv = Nng.lib().nng_msg_append(this.msg, data, data.length);
    }

    public int getBodyLen() {
        return Nng.lib().nng_msg_len(this.msg);
    }

    public ByteBuffer getBody() {
        int len = getBodyLen();
        if (len == 0) {
            return ByteBuffer.allocate(0);
        }

        BodyPointer body = Nng.lib().nng_msg_body(this.msg);
        if (body.getPointer() == Pointer.NULL) {
            // TODO: when does NNG return null here? is that possible?
            return null;
        }

        return body.getPointer().getByteBuffer(0, len);
    }
}
