package io.sisu.nng.internal;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import io.sisu.nng.jna.*;

import java.nio.Buffer;
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
    int nng_send(SocketStruct.ByValue socket, ByteBuffer data, Size size, int flags);
    int nng_socket_get(SocketStruct.ByValue s, String opt, Pointer val, SizeByReference size);
    int nng_socket_get_bool(SocketStruct.ByValue s, String opt, IntByReference bool);
    int nng_socket_get_int(SocketStruct.ByValue s, String opt, IntByReference intRef);
    int nng_socket_get_size(SocketStruct.ByValue s, String opt, SizeByReference size);
    int nng_socket_get_uint64(SocketStruct.ByValue s, String opt, UInt64ByReference uint64);
    int nng_socket_get_string(SocketStruct.ByValue s, String opt, Pointer strings);
    int nng_socket_get_ptr(SocketStruct.ByValue s, String opt, Pointer pointer);
    int nng_socket_get_ms(SocketStruct.ByValue s, String opt, IntByReference duration);
    int nng_socket_get_addr(SocketStruct.ByValue s, String opt, SockAddr addr);
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
    int nng_pipe_get(PipeStruct.ByValue pipe, String opt, Pointer value, SizeByReference size);
    int nng_pipe_get_int(PipeStruct.ByValue pipe, String opt, IntByReference value);
    int nng_pipe_get_ms(PipeStruct.ByValue pipe, String opt, IntByReference duration);
    int nng_pipe_get_ptr(PipeStruct.ByValue pipe, String opt, PointerByReference ref);
    int nng_pipe_get_addr(PipeStruct.ByValue pipe, String opt, SockAddr addr);
    // todo: NativeStringPointerByRef?
    int nng_pipe_get_string(PipeStruct.ByValue pipe, String opt, Pointer ref);
    int nng_pipe_get_size(PipeStruct.ByValue pipe, String opt, SizeByReference size);
    int nng_pipe_get_uint64(PipeStruct.ByValue pipe, String opt, UInt64 ref);


    // Messages
    int nng_msg_alloc(MessageByReference msgRef, Size size);
    int nng_msg_append(MessagePointer msg, ByteBuffer buf, Size size);
    BodyPointer nng_msg_body(MessagePointer msg);
    int nng_msg_chop(MessagePointer msg, Size size);
    int nng_msg_chop_u16(MessagePointer msg, UInt16 size);
    int nng_msg_chop_u32(MessagePointer msg, UInt32 size);
    int nng_msg_chop_u64(MessagePointer msg, UInt64 size);
    void nng_msg_clear(MessagePointer msg);
    int nng_msg_dup(MessageByReference dup, MessagePointer orig);
    int nng_msg_free(MessagePointer msg);
    PipeStruct.ByValue nng_msg_get_pipe(MessagePointer msg);
    int nng_msg_insert(MessagePointer msg, ByteBuffer buf, Size size);
    int nng_msg_insert_u16(MessagePointer msg, ByteBuffer buf, UInt16 size);
    int nng_msg_insert_u32(MessagePointer msg, ByteBuffer buf, UInt32 size);
    int nng_msg_insert_u64(MessagePointer msg, ByteBuffer buf, UInt64 size);
    int nng_msg_len(MessagePointer msg);
    int nng_msg_realloc(MessagePointer msg, Size size);
    int nng_msg_set_pipe(MessagePointer msg, PipeStruct.ByValue pipe);
    int nng_msg_trim(MessagePointer msg, Size size);
    int nng_msg_trim_u16(MessagePointer msg, UInt16 size);
    int nng_msg_trim_u32(MessagePointer msg, UInt32 size);
    int nng_msg_trim_u64(MessagePointer msg, UInt64 size);
    int nng_recvmsg(SocketStruct.ByValue socket, MessageByReference msg, int flags);
    int nng_sendmsg(SocketStruct.ByValue socket, MessagePointer msg, int flags);

    // Message Header Handling
    HeaderPointer nng_msg_header(MessagePointer msg);
    int nng_msg_header_append(MessagePointer msg, ByteBuffer buf, Size size);
    int nng_msg_header_chop(MessagePointer msg, Size size);
    int nng_msg_header_chop_u16(MessagePointer msg, UInt16 size);
    int nng_msg_header_chop_u32(MessagePointer msg, UInt32 size);
    int nng_msg_header_chop_u64(MessagePointer msg, UInt64 size);
    void nng_msg_header_clear(MessagePointer msg);
    int nng_msg_header_insert(MessagePointer msg, ByteBuffer buf, Size size);
    int nng_msg_header_len(MessagePointer msg);
    int nng_msg_header_trim(MessagePointer msg, Size size);
    int nng_msg_header_trim_u16(MessagePointer msg, UInt16 size);
    int nng_msg_header_trim_u32(MessagePointer msg, UInt32 size);
    int nng_msg_header_trim_u64(MessagePointer msg, UInt64 size);

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
    int nng_aio_set_iov(AioPointer aio, int niov, IovStruct[] iov); // TODO: iov
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
    int nng_url_parse(UrlByReference urlPointer, String rawUrl);
    int nng_url_free(UrlStruct url);

    // Supplemental API
    // TODO: Are these needed?

    // Byte Streams
    // TODO: Byte Streams

    // HTTP
    // TODO: HTTP
    void nng_http_conn_close(Pointer client);
    void nng_http_conn_read(Pointer client, AioPointer aio);
    void nng_http_conn_read_all(Pointer conn, AioPointer aio);
    void nng_http_conn_read_req(Pointer conn, HttpReqPointer req, AioPointer aio);
    void nng_http_conn_read_res(Pointer conn, HttpResPointer res, AioPointer aio);
    void nng_http_conn_write_all(Pointer conn, AioPointer aio);
    void nng_http_conn_write_req(Pointer conn, HttpReqPointer req, AioPointer aio);
    void nng_http_conn_write_res(Pointer conn, HttpResPointer res, AioPointer aio);
    // ...requests
    int nng_http_req_add_header(HttpReqPointer req, String key, String val);
    int nng_http_req_alloc(HttpReqPointerByReference req, UrlStruct url);
    int nng_http_req_copy_data(HttpReqPointer req, ByteBuffer data, Size size);
    int nng_http_req_del_header(HttpReqPointer req, String key);
    int nng_http_req_free(HttpReqPointer req);
    int nng_http_req_get_data(HttpReqPointer req, BodyPointerByReference ref, SizeByReference size);
    String nng_http_req_get_header(HttpReqPointer req, String key);
    String nng_http_req_get_method(HttpReqPointer req);
    String nng_http_req_get_uri(HttpReqPointer req);
    String nng_http_req_get_version(HttpReqPointer req);
    void nng_http_req_reset(HttpReqPointer req);
    int nng_http_req_set_data(HttpReqPointer req, ByteBuffer data, Size size);
    int nng_http_req_set_header(HttpReqPointer req, String key, String val);
    int nng_http_req_set_method(HttpReqPointer req, String method);
    int nng_http_req_set_uri(HttpReqPointer req, String uri);
    int nng_http_req_set_version(HttpReqPointer req, String version);
    // ...responses
    int nng_http_res_add_header(HttpResPointer req, String key, String val);
    int nng_http_res_alloc(HttpResPointerByReference ref);
    int nng_http_res_alloc_error(HttpResPointerByReference ref, short status);
    int nng_http_res_copy_data(HttpResPointer res, ByteBuffer data, Size size);
    int nng_http_res_del_header(HttpResPointer res, String key);
    int nng_http_res_free(HttpResPointer res);
    void nng_http_res_get_data(HttpResPointer res, BodyPointerByReference ref, SizeByReference size);
    String nng_http_res_get_header(HttpResPointer res, String key);
    String nng_http_res_get_reason(HttpResPointer res);
    short nng_http_res_get_status(HttpResPointer res);
    String nng_http_res_get_version(HttpResPointer res);
    void nng_http_res_reset(HttpResPointer res);
    int nng_http_res_set_data(HttpResPointer res, ByteBuffer data, Size size);
    int nng_http_res_set_header(HttpResPointer res, String key, String val);
    int nng_http_res_set_reason(HttpResPointer res, String reason);
    int nng_http_res_set_status(HttpResPointer res, short status);
    int nng_http_res_set_version(HttpResPointer res, String version);

    // ...clients
    int nng_http_client_alloc(HttpClientPointerByReference client, UrlStruct url);
    int nng_http_client_free(HttpClientPointer client);
    void nng_http_client_connect(HttpClientPointer client, AioPointer aio);

    // ...servers
    int nng_http_handler_alloc_static(HttpHandlerPointerByReference handlerRef, String path,
                                      Pointer data, Size size, String contentType);
    void nng_http_handler_free(HttpHandlerPointer handler);
    int nng_http_server_add_handler(HttpServerPointer server, HttpHandlerPointer handler);
    int nng_http_server_del_handler(HttpServerPointer server, HttpHandlerPointer handler);
    int nng_http_server_get_addr(HttpServerPointer server, SockAddr addr);
    //...tls
    int nng_http_server_hold(HttpServerPointerByReference serverRef, UrlStruct url);
    void nng_http_server_release(HttpServerPointer server);
    //todo: more
    int nng_http_server_start(HttpServerPointer server);
    void nng_http_server_stop(HttpServerPointer server);

}
