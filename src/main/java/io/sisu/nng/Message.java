package io.sisu.nng;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.sisu.nng.internal.BodyPointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.MessagePointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    public void append(byte[] data) throws NngException {
        int rv = Nng.lib().nng_msg_append(this.msg, data, data.length);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void append(String s, Charset charset) throws NngException {
        append(s.getBytes(charset));
    }

    public void append(String s) throws NngException {
        append(s, Charset.defaultCharset());
    }

    public int getBodyLen() {
        return Nng.lib().nng_msg_len(this.msg);
    }

    // Returns a reference to a Direct ByteBuffer that provides access to the Body of the Message
    public ByteBuffer getBody() {
        int len = getBodyLen();
        if (len == 0) {
            return ByteBuffer.allocateDirect(0);
        }

        BodyPointer body = Nng.lib().nng_msg_body(this.msg);
        if (body.getPointer() == Pointer.NULL) {
            // TODO: when does NNG return null here? is that possible?
            return null;
        }
        return body.getPointer().getByteBuffer(0, len);
    }

    public ByteBuffer getBodyOnHeap() {
        ByteBuffer body = getBody();
        if (body == null) {
            // TODO: does this happen? No idea.
            return null;
        }

        final ByteBuffer buffer = ByteBuffer.allocate(body.limit());

        // XXX: naive copy for now...could optimize with chunks later
        while (body.hasRemaining()) {
            buffer.put(body.get());
        }
        return buffer.flip();
    }
}
