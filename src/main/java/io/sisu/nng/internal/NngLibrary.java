package io.sisu.nng.internal;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.nio.ByteBuffer;

/**
 * Covers nng v1.3.2
 */
public interface NngLibrary extends Library {
    // Common
    String nng_strerror(int err);
    String nng_version();

    // Socket Functions (the most common functions, basically)
    int nng_close(SocketStruct.ByValue socket);
    int nng_dial(SocketStruct.ByValue socket, String url, Pointer dialer, int flags);
    int nng_listen(SocketStruct.ByValue socket, String url, Pointer listener, int flags);
    int nng_recv(SocketStruct.ByValue socket, ByteBuffer data, IntByReference size, int flags);
    int nng_send(SocketStruct.ByValue socket, ByteBuffer data, int size, int flags);
    int nng_socket_id(SocketStruct.ByValue socket);

    // Connection Management - Dialers
    int nng_dial_close(DialerStruct.ByValue dialer);
    int nng_dialer_create(DialerStruct dialer, SocketStruct.ByValue socket, String url);
    int nng_dialer_id(DialerStruct.ByValue dialer);
    int nng_dialer_start(DialerStruct.ByValue dialer, int flags);

    // Connection Management - Listeners
    int nng_listener_close(ListenerStruct.ByValue listener);
    int nng_listener_create(ListenerStruct listener, SocketStruct.ByValue socket, String url);
    int nng_listener_id(ListenerStruct.ByValue listener);
    int nng_listener_start(ListenerStruct.ByValue listener, int flags);

    // Pipes
    int nng_pipe_close(PipeStruct.ByValue pipe);
    DialerStruct.ByValue nng_pipe_dialer(PipeStruct.ByValue pipe);
    PipeStruct.ByValue nng_pipe_id(PipeStruct.ByValue pipe);
    ListenerStruct.ByValue nng_pipe_listener(PipeStruct.ByValue pipe);
    int nng_pipe_notify(SocketStruct.ByValue socket, int pipeEvent, Callback cb, Pointer arg);
    SocketStruct.ByValue nng_pipe_socket(PipeStruct.ByValue pipe);

    // Messages
    int nng_msg_alloc(MessageByReference msgRef, int size);
    int nng_msg_append(MessagePointer msg, ByteBuffer buf, int size);
    BodyPointer nng_msg_body(MessagePointer msg);
    int nng_msg_chop(MessagePointer msg, int size);
    int nng_msg_chop_u16(MessagePointer msg, short size);
    int nng_msg_chop_u32(MessagePointer msg, int size);
    int nng_msg_chop_u64(MessagePointer msg, long size);
    void nng_msg_clear(MessagePointer msg);
    int nng_msg_dup(MessageByReference dup, MessagePointer orig);
    int nng_msg_free(MessagePointer msg);
    PipeStruct.ByValue nng_msg_get_pipe(MessagePointer msg);
    int nng_msg_insert(MessagePointer msg, ByteBuffer buf, int size);
    int nng_msg_insert_u16(MessagePointer msg, ByteBuffer buf, short size);
    int nng_msg_insert_u32(MessagePointer msg, ByteBuffer buf, int size);
    int nng_msg_insert_u64(MessagePointer msg, ByteBuffer buf, long size);
    int nng_msg_len(MessagePointer msg);
    int nng_msg_realloc(MessagePointer msg, int size);
    int nng_msg_set_pipe(MessagePointer msg, PipeStruct.ByValue pipe);
    int nng_msg_trim(MessagePointer msg, int size);
    int nng_msg_trim_u16(MessagePointer msg, int size);
    int nng_msg_trim_u32(MessagePointer msg, short size);
    int nng_msg_trim_u64(MessagePointer msg, long size);
    int nng_recvmsg(SocketStruct.ByValue socket, MessageByReference msg, int flags);
    int nng_sendmsg(SocketStruct.ByValue socket, MessagePointer msg, int flags);

    // Message Header Handling
    Pointer nng_msg_header(MessagePointer msg);
    int nng_msg_header_append(MessagePointer msg, ByteBuffer buf, int size);
    int nng_msg_header_chop(MessagePointer msg, int size);
    int nng_msg_header_chop_u16(MessagePointer msg, short size);
    int nng_msg_header_chop_u32(MessagePointer msg, int size);
    int nng_msg_header_chop_u64(MessagePointer msg, long size);
    void nng_msg_header_clear(MessagePointer msg);
    int nng_msg_header_insert(MessagePointer msg, ByteBuffer buf, int size);
    int nng_msg_header_len(MessagePointer msg);
    int nng_msg_header_trim(MessagePointer msg, int size);
    int nng_msg_header_trim_u16(MessagePointer msg, short size);
    int nng_msg_header_trim_u32(MessagePointer msg, int size);
    int nng_msg_header_trim_u64(MessagePointer msg, long size);

    // Asynchronous Operations
    void nng_aio_abort(AioPointer aio, int err);
    int nng_aio_alloc(AioPointerByReference aiop, Callback cb, Pointer arg);
    boolean nng_aio_begin(AioPointer aio);
    void nng_aio_cancel(AioPointer aio);
    int nng_aio_count(AioPointer aio);
    void nng_aio_defer(AioPointer aio, Callback fn, Pointer arg);
    void nng_aio_finish(AioPointer aio, int err);
    void nng_aio_free(AioPointer aio);
    Pointer nng_aio_get_input(AioPointer aio, int index);
    MessagePointer nng_aio_get_msg(AioPointer aio);
    Pointer nng_aio_get_output(AioPointer aio, int index);
    int nng_aio_result(AioPointer aio);
    void nng_aio_set_input(AioPointer aio, int index, Pointer param);
    int nng_aio_set_iov(AioPointer aio, int niov, Pointer iov); // TODO: iov
    void nng_aio_set_msg(AioPointer aio, MessagePointer msg);
    void nng_aio_set_output(AioPointer aio, int index, Pointer result);
    void nng_aio_set_timeout(AioPointer aio, int timeoutMillis);
    void nng_aio_stop(AioPointer aio);
    void nng_aio_wait(AioPointer aio);
    void nng_recv_aio(SocketStruct.ByValue socket, AioPointer aio);
    void nng_send_aio(SocketStruct.ByValue socket, AioPointer aio);
    void nng_sleep_aio(int durationMills, AioPointer aio);


    // Protocols
    int nng_bus0_open(SocketStruct socket);
    int nng_bus0_open_raw(SocketStruct socket);
    int nng_pair0_open(SocketStruct socket);
    int nng_pair0_open_raw(SocketStruct socket);
    int nng_pair1_open(SocketStruct socket);
    int nng_pair1_open_raw(SocketStruct socket);
    int nng_pub0_open(SocketStruct socket);
    int nng_pub0_open_raw(SocketStruct socket);
    int nng_pull0_open(SocketStruct socket);
    int nng_pull0_open_raw(SocketStruct socket);
    int nng_push0_open(SocketStruct socket);
    int nng_push0_open_raw(SocketStruct socket);
    int nng_rep0_open(SocketStruct socket);
    int nng_rep0_open_raw(SocketStruct socket);
    int nng_req0_open(SocketStruct socket);
    int nng_req0_open_raw(SocketStruct socket);
    int nng_respondent0_open(SocketStruct socket);
    int nng_respondent0_open_raw(SocketStruct socket);
    int nng_sub0_open(SocketStruct socket);
    int nng_sub0_open_raw(SocketStruct socket);
    int nng_surveyor0_open(SocketStruct socket);
    int nng_surveyor0_open_raw(SocketStruct socket);

    // Transports
    int nng_inproc_register();
    int nng_ipc_register();
    int nng_tcp_register();
    int nng_tls_register();
    int nng_ws_register();
    int nng_wss_register();
    int nng_zt_register();

    // Protocol Contexts
    int nng_ctx_close(ContextStruct.ByValue context);
    int nng_ctx_id(ContextStruct.ByValue context);
    int nng_ctx_open(ContextStruct context, SocketStruct.ByValue socket);
    void nng_ctx_recv(ContextStruct.ByValue context, AioPointer aio);
    void nng_ctx_send(ContextStruct.ByValue context, AioPointer aio);

    // URL Object
    // TODO: URL Object

    // Supplemental API
    // TODO: Are these needed?

    // Byte Streams
    // TODO: Byte Streams

    // HTTP
    // TODO: HTTP
}
