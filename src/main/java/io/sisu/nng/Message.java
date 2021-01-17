package io.sisu.nng;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.sisu.nng.internal.BodyPointer;
import io.sisu.nng.internal.HeaderPointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.MessagePointer;
import io.sisu.nng.internal.jna.Size;
import io.sisu.nng.internal.jna.UInt16;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.jna.UInt32ByReference;

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
        int rv = Nng.lib().nng_msg_alloc(ref, new Size(size));
        if (rv != 0) {
            final String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        msg = ref.getMessage();
    }

    public Message(MessagePointer pointer) {
        this.msg = pointer;
    }


    public void appendToHeader(ByteBuffer data) throws NngException {
        final int len = data.limit() - data.position();
        int rv = Nng.lib().nng_msg_header_append(msg, data, new Size(len));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void insertToHeader(ByteBuffer data) throws NngException {
        final int len = data.limit() - data.position();
        int rv = Nng.lib().nng_msg_header_insert(msg, data, new Size(len));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public MessagePointer getMessagePointer() {
        return msg;
    }

    public boolean isValid() {
        return valid;
    }

    public void append(ByteBuffer data) throws NngException {
        final int len = data.limit() - data.position();
        int rv = Nng.lib().nng_msg_append(msg, data, new Size(len));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void append(byte[] data) throws NngException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();
        append(buffer);
    }

    public void append(String s, Charset charset) throws NngException {
        append(Native.toByteArray(s, charset));
    }

    public void append(String s) throws NngException {
        append(s, StandardCharsets.UTF_8);
    }

    public void appendU16(int val) {
        UInt16 uInt16 = new UInt16(val);

    }

    public int getBodyLen() {
        return Nng.lib().nng_msg_len(msg);
    }

    public int getHeaderLen() {
        return Nng.lib().nng_msg_header_len(msg);
    }

    public ByteBuffer getHeader() {
        int len = getHeaderLen();
        if (len == 0) {
            return ByteBuffer.allocateDirect(0);
        }

        HeaderPointer header = Nng.lib().nng_msg_header(msg);
        if (header.getPointer() == Pointer.NULL) {
            // TODO: when does NNG return null here? is that possible?
            return null;
        }
        return header.getPointer().getByteBuffer(0, len);
    }

    // Returns a reference to a Direct ByteBuffer that provides access to the Body of the Message
    public ByteBuffer getBody() {
        int len = getBodyLen();
        if (len == 0) {
            return ByteBuffer.allocate(0);
        }

        BodyPointer body = Nng.lib().nng_msg_body(msg);
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

        ByteBuffer buffer = ByteBuffer.allocate(body.limit());

        // XXX: naive copy for now...could optimize with chunks later
        while (body.hasRemaining()) {
            buffer.put(body.get());
        }
        buffer.flip();

        return buffer;
    }

    public void trim(int len) throws NngException {
        int rv = Nng.lib().nng_msg_trim(msg, new Size(len));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public int trim32Bits() throws NngException {
        UInt32ByReference ref = new UInt32ByReference();
        int rv = Nng.lib().nng_msg_trim_u32(msg, ref);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        return ref.getUInt32().convert();
    }
}
