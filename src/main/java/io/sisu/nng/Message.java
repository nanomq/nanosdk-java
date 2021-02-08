package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.BodyPointer;
import io.sisu.nng.internal.HeaderPointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.MessagePointer;
import io.sisu.nng.internal.jna.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wraps the native NNG message structure and provides convenience methods for ferrying data to and
 * from the JVM.
 *
 * Note: Per NNG's design, Message's may change "ownership" when being sent, which is a paradigm
 *       not native to Java. The <i>valid</i> flag is designed to indicate if it's safe to
 *       continue mutating the Message in the JVM (<i>valid: true</i>) or if it's expected
 *       that the Message is owned by the native NNG layer (and expected to have its backing
 *       memory freed automatically).
 */
public class Message implements AutoCloseable {

    // Global counter of allocated Messages
    public static AtomicInteger created = new AtomicInteger(0);
    // Global counter of freed Messages
    public static AtomicInteger freed = new AtomicInteger(0);
    // Global counter of invalidated Messages (i.e. ownership passed to native memory)
    public static AtomicInteger invalidated = new AtomicInteger(0);

    // Indicates if the Message instance is still owned by the JVM
    protected AtomicBoolean valid = new AtomicBoolean(false);

    private MessagePointer msg;

    /**
     * Allocate a new Message with a 0-byte Body
     * @throws NngException
     */
    public Message() throws NngException {
        this(0);
    }

    /**
     * Allocate a new NNG Message of the given size. Equivalent to a call to <pre>nng_msg_alloc</pre>
     *
     * @param size number of bytes to allocate for the message
     * @throws NngException if nng_msg_alloc fails
     */
    public Message(int size) throws NngException {
        final MessageByReference ref = new MessageByReference();
        int rv = Nng.lib().nng_msg_alloc(ref, new Size(size));
        if (rv != 0) {
            final String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        msg = ref.getMessage();
        valid.set(true);
        created.incrementAndGet();
    }

    public Message(Pointer pointer) throws NngException {
        this(new MessagePointer(pointer));
    }

    public Message(MessagePointer pointer) throws NngException {
        if (pointer.getPointer() == Pointer.NULL) {
            throw new NngException("attempt to create a Message from a null Pointer");
        }
        this.msg = pointer;
        valid.set(true);
        created.incrementAndGet();
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

    public void append(Pointer p, int size) throws NngException {
        int rv = Nng.lib().nng_msg_append(msg, p, new Size(size));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void append(ByteBuffer data) throws NngException {
        final int rv;

        if (data.hasArray()) {
            byte[] buf = data.slice().array();
            rv = Nng.lib().nng_msg_append(msg, buf, buf.length);
        } else {
            rv = Nng.lib().nng_msg_append(msg, data,
                    new Size(data.limit() - data.position()));
        }
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void append(byte[] data) throws NngException {
        int rv = Nng.lib().nng_msg_append(msg, data, data.length);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    /**
     * Convenience method for appending a Java String to the Message body using the given Charset.
     *
     * @param s the String to append
     * @param charset the charset used for interpreting the String
     * @throws NngException if the call to nng_msg_append fails
     */
    public void append(String s, Charset charset) throws NngException {
        append(s.getBytes(charset));
    }

    public void append(String s) throws NngException {
        append(s, StandardCharsets.UTF_8);
    }

    public void appendU16(int i) throws NngException {
        UInt16 val = new UInt16(i);
        int rv = Nng.lib().nng_msg_append_u16(msg, val);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void appendU32(int i) throws NngException {
        UInt32 val = new UInt32(i);
        int rv = Nng.lib().nng_msg_append_u32(msg, val);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void appendU64(int i) throws NngException {
        UInt64 val = new UInt64(i);
        int rv = Nng.lib().nng_msg_append_u64(msg, val);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void insertU16(int i) throws NngException {
        int rv = Nng.lib().nng_msg_insert_u16(msg, new UInt16(i));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void insertU32(int i) throws NngException {
        int rv = Nng.lib().nng_msg_insert_u32(msg, new UInt32(i));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void insertU64(int i) throws NngException {
        int rv = Nng.lib().nng_msg_insert_u64(msg, new UInt64(i));
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
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

    /**
     * Returns a native ByteBuffer backed by the message's body data.
     *
     * XXX: This ByteBuffer is backed by the Body portion of the nng_msg and MUST NOT BE GARBAGE
     * COLLECTED BEFORE THE MESSAGE ITSELF!
     *
     * TODO: this is terribly unsafe and need to be redesigned as put() operations to allow direct
     * access to the body without ByteBuffers.
     *
     * @return new native ByteBuffer or an empty ByteBuffer if body length is zero
     */
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

    /**
     * Convenience method to extract data from native memory onto the JVM by allocating a non-native
     * ByteBuffer and copying the data into it.
     *
     * @return non-native ByteBuffer containing a copy of the message body data
     */
    public ByteBuffer getBodyCopy() {
        final int len = getBodyLen();

        BodyPointer body = Nng.lib().nng_msg_body(msg);
        ByteBuffer buffer = ByteBuffer.wrap(body.getPointer().getByteArray(0, len));
        buffer.order(ByteOrder.nativeOrder());

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
        return ref.getUInt32().intValue();
    }

    /**
     * Attempt to deallocate the underlying nng_msg, releasing unmanaged memory.
     *
     * This action only occurs if the Message is currently valid and owned by the JVM. If freed, the
     * Message is no longer valid.
     *
     * Note: it's recommended that the programmer liberally make use for {@link #free()} when they
     * know their {@link Message} instance is no longer required and <u>not</u> to rely solely on
     * the garbage collector. Native heap fragmentation can occur if large quantities of messages
     * are freed, as will happen if waiting for a gc event to clean up. The end result typically is
     * excessive memory use and in the worst cases result in out of memory conditions for the JVM
     * process.
     */
    public void free() {
        if (valid.compareAndSet(true, false)) {
            // System.out.println("JVM freeing Message " + this);
            Nng.lib().nng_msg_free(msg);
            freed.incrementAndGet();
        }
    }

    /**
     * Set the Message state back to valid and owned by the JVM.
     *
     * N.b. This should be used typically only when a Send of the Message fails and the nng library
     * didn't take ownership.
     */
    public void setValid() {
        if (valid.compareAndSet(false, true)) {
            invalidated.decrementAndGet();
        }
    }

    /**
     * Set the message invalid, preventing the JVM from attempting to free the underlying native
     * memory during garbage collection.
     */
    public void setInvalid() {
        if (valid.compareAndSet(true, false)) {
            invalidated.incrementAndGet();
        }
    }

    /**
     * Check if the Message is still valid or not.
     *
     * @return boolean whether or not the Message is valid
     */
    public boolean isValid() {
        return valid.get();
    }

    /**
     * Cleanup the Message, attempting to free it if required.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            free();
        } finally {
            super.finalize();
        }
    }

    @Override
    public void close() {
        free();
    }
}
