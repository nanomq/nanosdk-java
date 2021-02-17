package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import io.sisu.nng.internal.jna.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class NngDirectLibrary implements NngLibrary {

    public NngDirectLibrary() {
        Native.register("nng");
    }
    // Common
    public native String nng_strerror(int err);
    public native String nng_version();

    // Socket Functions (the most common functions, basically)
    public native int nng_close(SocketStruct.ByValue socket);
    public native int nng_dial(SocketStruct.ByValue socket, String url, Pointer dialer, int flags);
    public native int nng_listen(SocketStruct.ByValue socket, String url, Pointer listener, int flags);
    public native int nng_recv(SocketStruct.ByValue socket, ByteBuffer data, SizeByReference size, int flags);
    public native int nng_send(SocketStruct.ByValue socket, ByteBuffer data, Size size, int flags);

    public native int nng_socket_get(SocketStruct.ByValue s, String opt, Pointer val, SizeByReference size);
    public native int nng_socket_get_bool(SocketStruct.ByValue s, String opt, IntByReference bool);
    public native int nng_socket_get_int(SocketStruct.ByValue s, String opt, IntByReference intRef);
    public native int nng_socket_get_size(SocketStruct.ByValue s, String opt, SizeByReference size);
    public native int nng_socket_get_uint64(SocketStruct.ByValue s, String opt, UInt64ByReference uint64);
    public native int nng_socket_get_string(SocketStruct.ByValue s, String opt, Pointer strings);
    public native int nng_socket_get_ptr(SocketStruct.ByValue s, String opt, Pointer pointer);
    public native int nng_socket_get_ms(SocketStruct.ByValue s, String opt, IntByReference duration);
    public native int nng_socket_get_addr(SocketStruct.ByValue s, String opt, SockAddr addr);

    public native int nng_socket_set(SocketStruct.ByValue s, String opt, Pointer val, Size size);
    public native int nng_socket_set_bool(SocketStruct.ByValue s, String opt, boolean bool);
    public native int nng_socket_set_int(SocketStruct.ByValue s, String opt, int intRef);
    public native int nng_socket_set_size(SocketStruct.ByValue s, String opt, Size size);
    public native int nng_socket_set_uint64(SocketStruct.ByValue s, String opt, UInt64 uint64);
    public native int nng_socket_set_string(SocketStruct.ByValue s, String opt, String string);
    public native int nng_socket_set_ptr(SocketStruct.ByValue s, String opt, Pointer pointer);
    public native int nng_socket_set_ms(SocketStruct.ByValue s, String opt, int duration);
    public native int nng_socket_id(SocketStruct.ByValue socket);

    // Connection Management - Dialers
    public native int nng_dialer_close(DialerStruct.ByValue dialer);
    public native int nng_dialer_create(DialerStruct dialer, SocketStruct.ByValue socket, String url);
    public native int nng_dialer_id(DialerStruct.ByValue dialer);
    public native int nng_dialer_start(DialerStruct.ByValue dialer, int flags);

    // Connection Management - Listeners
    public native int nng_listener_close(ListenerStruct.ByValue listener);
    public native int nng_listener_create(ListenerStruct listener, SocketStruct.ByValue socket, String url);
    public native int nng_listener_id(ListenerStruct.ByValue listener);
    public native int nng_listener_start(ListenerStruct.ByValue listener, int flags);

    // Pipes
    public native int nng_pipe_close(PipeStruct.ByValue pipe);
    public native DialerStruct.ByValue nng_pipe_dialer(PipeStruct.ByValue pipe);
    public native PipeStruct.ByValue nng_pipe_id(PipeStruct.ByValue pipe);
    public native ListenerStruct.ByValue nng_pipe_listener(PipeStruct.ByValue pipe);
    public native int nng_pipe_notify(SocketStruct.ByValue socket, int pipeEvent, NngCallback cb, Pointer arg);
    public native SocketStruct.ByValue nng_pipe_socket(PipeStruct.ByValue pipe);
    public native int nng_pipe_get(PipeStruct.ByValue pipe, String opt, Pointer value, SizeByReference size);
    public native int nng_pipe_get_int(PipeStruct.ByValue pipe, String opt, IntByReference value);
    public native int nng_pipe_get_ms(PipeStruct.ByValue pipe, String opt, IntByReference duration);
    public native int nng_pipe_get_ptr(PipeStruct.ByValue pipe, String opt, PointerByReference ref);
    public native int nng_pipe_get_addr(PipeStruct.ByValue pipe, String opt, SockAddr addr);
    // todo: NativeStringPointerByRef?
    public native int nng_pipe_get_string(PipeStruct.ByValue pipe, String opt, Pointer ref);
    public native int nng_pipe_get_size(PipeStruct.ByValue pipe, String opt, SizeByReference size);
    public native int nng_pipe_get_uint64(PipeStruct.ByValue pipe, String opt, UInt64 ref);


    // Messages
    public native int nng_msg_alloc(MessageByReference msgRef, Size size);
    public native int nng_msg_append(MessagePointer msg, Pointer buf, Size size);
    public native int nng_msg_append(MessagePointer msg, Buffer buf, Size size);
    public native int nng_msg_append(MessagePointer msg, byte[] buf, int size);
    public native int nng_msg_append_u16(MessagePointer msg, UInt16 val);
    public native int nng_msg_append_u32(MessagePointer msg, UInt32 val);
    public native int nng_msg_append_u64(MessagePointer msg, UInt64 val);
    public native BodyPointer nng_msg_body(MessagePointer msg);
    public native int nng_msg_chop(MessagePointer msg, Size size);
    public native int nng_msg_chop_u16(MessagePointer msg, UInt16 size);
    public native int nng_msg_chop_u32(MessagePointer msg, UInt32 size);
    public native int nng_msg_chop_u64(MessagePointer msg, UInt64 size);
    public native void nng_msg_clear(MessagePointer msg);
    public native int nng_msg_dup(MessageByReference dup, MessagePointer orig);
    public native void nng_msg_free(MessagePointer msg);
    public native PipeStruct.ByValue nng_msg_get_pipe(MessagePointer msg);
    public native int nng_msg_insert(MessagePointer msg, ByteBuffer buf, Size size);
    public native int nng_msg_insert_u16(MessagePointer msg, UInt16 size);
    public native int nng_msg_insert_u32(MessagePointer msg, UInt32 size);
    public native int nng_msg_insert_u64(MessagePointer msg, UInt64 size);
    public native int nng_msg_len(MessagePointer msg);
    public native int nng_msg_realloc(MessagePointer msg, Size size);
    public native int nng_msg_set_pipe(MessagePointer msg, PipeStruct.ByValue pipe);
    public native int nng_msg_trim(MessagePointer msg, Size size);
    public native int nng_msg_trim_u16(MessagePointer msg, UInt16ByReference size);
    public native int nng_msg_trim_u32(MessagePointer msg, UInt32ByReference size);
    public native int nng_msg_trim_u64(MessagePointer msg, UInt64ByReference size);
    public native int nng_recvmsg(SocketStruct.ByValue socket, MessageByReference msg, int flags);
    public native int nng_sendmsg(SocketStruct.ByValue socket, MessagePointer msg, int flags);

    // Message Header Handling
    public native HeaderPointer nng_msg_header(MessagePointer msg);
    public native int nng_msg_header_append(MessagePointer msg, ByteBuffer buf, Size size);
    public native int nng_msg_header_chop(MessagePointer msg, Size size);
    public native int nng_msg_header_chop_u16(MessagePointer msg, UInt16 size);
    public native int nng_msg_header_chop_u32(MessagePointer msg, UInt32 size);
    public native int nng_msg_header_chop_u64(MessagePointer msg, UInt64 size);
    public native void nng_msg_header_clear(MessagePointer msg);
    public native int nng_msg_header_insert(MessagePointer msg, ByteBuffer buf, Size size);
    public native int nng_msg_header_len(MessagePointer msg);
    public native int nng_msg_header_trim(MessagePointer msg, Size size);
    public native int nng_msg_header_trim_u16(MessagePointer msg, UInt16 size);
    public native int nng_msg_header_trim_u32(MessagePointer msg, UInt32 size);
    public native int nng_msg_header_trim_u64(MessagePointer msg, UInt64 size);

    // Asynchronous Operations
    public native void nng_aio_abort(AioPointer aio, int err);
    public native int nng_aio_alloc(AioPointerByReference aiop, NngCallback cb, Pointer arg);
    public native boolean nng_aio_begin(AioPointer aio);
    public native void nng_aio_cancel(AioPointer aio);
    public native int nng_aio_count(AioPointer aio);
    public native void nng_aio_defer(AioPointer aio, NngCallback fn, Pointer arg);
    public native void nng_aio_finish(AioPointer aio, int err);
    public native void nng_aio_free(AioPointer aio);
    public native Pointer nng_aio_get_input(AioPointer aio, int index);
    public native Pointer nng_aio_get_msg(AioPointer aio);
    public native Pointer nng_aio_get_output(AioPointer aio, int index);
    public native int nng_aio_result(AioPointer aio);
    public native void nng_aio_set_input(AioPointer aio, int index, Pointer param);

    // XXX: NOT SUPPORTED VIA DIRECT MAPPING?
    public native int nng_aio_set_iov(AioPointer aio, int niov, Pointer iov); // TODO: iov
    public int nng_aio_set_iov(AioPointer aio, int niov, IovStruct[] iov) {
        return -1;
    }

    public native void nng_aio_set_msg(AioPointer aio, MessagePointer msg);
    public native void nng_aio_set_output(AioPointer aio, int index, Pointer result);
    public native void nng_aio_set_timeout(AioPointer aio, int timeoutMillis);
    public native void nng_aio_stop(AioPointer aio);
    public native void nng_aio_wait(AioPointer aio);
    public native void nng_recv_aio(SocketStruct.ByValue socket, AioPointer aio);
    public native void nng_send_aio(SocketStruct.ByValue socket, AioPointer aio);
    public native void nng_sleep_aio(int durationMills, AioPointer aio);


    // Protocols
    public native int nng_bus0_open(SocketStruct socket);
    public native int nng_bus0_open_raw(SocketStruct socket);
    public native int nng_pair0_open(SocketStruct socket);
    public native int nng_pair0_open_raw(SocketStruct socket);
    public native int nng_pair1_open(SocketStruct socket);
    public native int nng_pair1_open_raw(SocketStruct socket);
    public native int nng_pub0_open(SocketStruct socket);
    public native int nng_pub0_open_raw(SocketStruct socket);
    public native int nng_pull0_open(SocketStruct socket);
    public native int nng_pull0_open_raw(SocketStruct socket);
    public native int nng_push0_open(SocketStruct socket);
    public native int nng_push0_open_raw(SocketStruct socket);
    public native int nng_rep0_open(SocketStruct socket);
    public native int nng_rep0_open_raw(SocketStruct socket);
    public native int nng_req0_open(SocketStruct socket);
    public native int nng_req0_open_raw(SocketStruct socket);
    public native int nng_respondent0_open(SocketStruct socket);
    public native int nng_respondent0_open_raw(SocketStruct socket);
    public native int nng_sub0_open(SocketStruct socket);
    public native int nng_sub0_open_raw(SocketStruct socket);
    public native int nng_surveyor0_open(SocketStruct socket);
    public native int nng_surveyor0_open_raw(SocketStruct socket);

    // Transports
    /*
    public native int nng_inproc_register();
    public native int nng_ipc_register();
    public native int nng_tcp_register();
    public native int nng_tls_register();
    public native int nng_ws_register();
    public native int nng_wss_register();
    public native int nng_zt_register();
    */
    // Protocol Contexts
    public native int nng_ctx_close(ContextStruct.ByValue context);
    public native int nng_ctx_id(ContextStruct.ByValue context);
    public native int nng_ctx_open(ContextStruct context, SocketStruct.ByValue socket);
    public native void nng_ctx_recv(ContextStruct.ByValue context, AioPointer aio);
    public native void nng_ctx_send(ContextStruct.ByValue context, AioPointer aio);

    public native int nng_ctx_get(ContextStruct.ByValue s, String opt, Pointer val, SizeByReference size);
    public native int nng_ctx_get_bool(ContextStruct.ByValue s, String opt, IntByReference bool);
    public native int nng_ctx_get_int(ContextStruct.ByValue s, String opt, IntByReference intRef);
    public native int nng_ctx_get_size(ContextStruct.ByValue s, String opt, SizeByReference size);
    public native int nng_ctx_get_uint64(ContextStruct.ByValue s, String opt, UInt64ByReference uint64);
    public native int nng_ctx_get_string(ContextStruct.ByValue s, String opt, Pointer strings);
    public native int nng_ctx_get_ptr(ContextStruct.ByValue s, String opt, Pointer pointer);
    public native int nng_ctx_get_ms(ContextStruct.ByValue s, String opt, IntByReference duration);
    public native int nng_ctx_get_addr(ContextStruct.ByValue s, String opt, SockAddr addr);

    public native int nng_ctx_set(ContextStruct.ByValue s, String opt, Pointer val, Size size);
    public native int nng_ctx_set_bool(ContextStruct.ByValue s, String opt, boolean bool);
    public native int nng_ctx_set_int(ContextStruct.ByValue s, String opt, int intRef);
    public native int nng_ctx_set_size(ContextStruct.ByValue s, String opt, Size size);
    public native int nng_ctx_set_uint64(ContextStruct.ByValue s, String opt, UInt64 uint64);
    public native int nng_ctx_set_string(ContextStruct.ByValue s, String opt, String string);
    public native int nng_ctx_set_ptr(ContextStruct.ByValue s, String opt, Pointer pointer);
    public native int nng_ctx_set_ms(ContextStruct.ByValue s, String opt, int duration);

    // URL Object
    // TODO: URL Object
    public native int nng_url_parse(UrlByReference urlPointer, String rawUrl);
    public native void nng_url_free(UrlStruct url);

    // Supplemental API
    // TODO: Are these needed?

    // Byte Streams
    // TODO: Byte Streams

    // HTTP
    // TODO: HTTP
    public native void nng_http_conn_close(Pointer client);
    public native void nng_http_conn_read(Pointer client, AioPointer aio);
    public native void nng_http_conn_read_all(Pointer conn, AioPointer aio);
    public native void nng_http_conn_read_req(Pointer conn, HttpReqPointer req, AioPointer aio);
    public native void nng_http_conn_read_res(Pointer conn, HttpResPointer res, AioPointer aio);
    public native void nng_http_conn_write_all(Pointer conn, AioPointer aio);
    public native void nng_http_conn_write_req(Pointer conn, HttpReqPointer req, AioPointer aio);
    public native void nng_http_conn_write_res(Pointer conn, HttpResPointer res, AioPointer aio);
    // ...requests
    public native int nng_http_req_add_header(HttpReqPointer req, String key, String val);
    public native int nng_http_req_alloc(HttpReqPointerByReference req, UrlStruct url);
    public native int nng_http_req_copy_data(HttpReqPointer req, ByteBuffer data, Size size);
    public native int nng_http_req_del_header(HttpReqPointer req, String key);
    public native void nng_http_req_free(HttpReqPointer req);
    public native int nng_http_req_get_data(HttpReqPointer req, BodyPointerByReference ref, SizeByReference size);
    public native String nng_http_req_get_header(HttpReqPointer req, String key);
    public native String nng_http_req_get_method(HttpReqPointer req);
    public native String nng_http_req_get_uri(HttpReqPointer req);
    public native String nng_http_req_get_version(HttpReqPointer req);
    public native void nng_http_req_reset(HttpReqPointer req);
    public native int nng_http_req_set_data(HttpReqPointer req, ByteBuffer data, Size size);
    public native int nng_http_req_set_header(HttpReqPointer req, String key, String val);
    public native int nng_http_req_set_method(HttpReqPointer req, String method);
    public native int nng_http_req_set_uri(HttpReqPointer req, String uri);
    public native int nng_http_req_set_version(HttpReqPointer req, String version);
    // ...responses
    public native int nng_http_res_add_header(HttpResPointer req, String key, String val);
    public native int nng_http_res_alloc(HttpResPointerByReference ref);
    public native int nng_http_res_alloc_error(HttpResPointerByReference ref, short status);
    public native int nng_http_res_copy_data(HttpResPointer res, ByteBuffer data, Size size);
    public native int nng_http_res_del_header(HttpResPointer res, String key);
    public native void nng_http_res_free(HttpResPointer res);
    public native void nng_http_res_get_data(HttpResPointer res, BodyPointerByReference ref, SizeByReference size);
    public native String nng_http_res_get_header(HttpResPointer res, String key);
    public native String nng_http_res_get_reason(HttpResPointer res);
    public native short nng_http_res_get_status(HttpResPointer res);
    public native String nng_http_res_get_version(HttpResPointer res);
    public native void nng_http_res_reset(HttpResPointer res);
    public native int nng_http_res_set_data(HttpResPointer res, ByteBuffer data, Size size);
    public native int nng_http_res_set_header(HttpResPointer res, String key, String val);
    public native int nng_http_res_set_reason(HttpResPointer res, String reason);
    public native int nng_http_res_set_status(HttpResPointer res, short status);
    public native  int nng_http_res_set_version(HttpResPointer res, String version);

    // ...clients
    public native int nng_http_client_alloc(HttpClientPointerByReference client, UrlStruct url);
    public native void nng_http_client_free(HttpClientPointer client);
    public native void nng_http_client_connect(HttpClientPointer client, AioPointer aio);

    // ...servers
    public native int nng_http_handler_alloc_static(HttpHandlerPointerByReference handlerRef, String path,
                                      Pointer data, Size size, String contentType);
    public native void nng_http_handler_free(HttpHandlerPointer handler);
    public native int nng_http_server_add_handler(HttpServerPointer server, HttpHandlerPointer handler);
    public native int nng_http_server_del_handler(HttpServerPointer server, HttpHandlerPointer handler);
    public native int nng_http_server_get_addr(HttpServerPointer server, SockAddr addr);
    //...tls
    public native int nng_http_server_hold(HttpServerPointerByReference serverRef, UrlStruct url);
    public native void nng_http_server_release(HttpServerPointer server);
    //todo: more
    public native int nng_http_server_start(HttpServerPointer server);
    public native void nng_http_server_stop(HttpServerPointer server);

    public native int nng_tls_config_alloc(TlsConfigByReference ref, int mode);
    public native int nng_tls_config_auth_mode(TlsConfigPointer cfg, int mode);
    public native int nng_tls_config_ca_chain(TlsConfigPointer cfg, String chain, String crl);
    public native  int nng_tls_config_ca_file(TlsConfigPointer cfg, String path);
    public native int nng_tls_config_cert_key_file(TlsConfigPointer cfg, String pathToKey, String password);
    public native int nng_tls_config_free(TlsConfigPointer cfg);
    public native int nng_tls_config_hold(TlsConfigPointer cfg);
    public native int nng_tls_config_own_cert(TlsConfigPointer cfg, String cert, String key, String password);
    public native int nng_tls_config_server_name(TlsConfigPointer cfg, String serverName);
    public native int nng_tls_config_version(TlsConfigPointer cfg, int minVersion, int maxVersion);
    public native String nng_tls_engine_description();
    public native boolean nng_tls_engine_fips_mode();
    public native String nng_tls_engine_name();
    // XXX: Memtrack...requires patch to NNG that tracks memory allocation
    // public native int nng_memtrack(UInt64ByReference alloc, UInt64ByReference freed);
}
