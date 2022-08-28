package io.sisu.nng.internal;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import io.sisu.nng.internal.jna.*;
import io.sisu.nng.internal.mqtt.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Covers nng v1.6.0
 */
public interface NngLibrary extends Library {
    // Common
    String nng_strerror(int err);

    String nng_version();

    // Socket Functions (the most common functions, basically)
    int nng_close(SocketStruct.ByValue socket);

    int nng_dial(SocketStruct.ByValue socket, String url, Pointer dialer, int flags);

    int nng_listen(SocketStruct.ByValue socket, String url, Pointer listener, int flags);

    int nng_recv(SocketStruct.ByValue socket, ByteBuffer data, SizeByReference size, int flags);

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

    int nng_socket_set(SocketStruct.ByValue s, String opt, Pointer val, Size size);

    int nng_socket_set_bool(SocketStruct.ByValue s, String opt, boolean bool);

    int nng_socket_set_int(SocketStruct.ByValue s, String opt, int intRef);

    int nng_socket_set_size(SocketStruct.ByValue s, String opt, Size size);

    int nng_socket_set_uint64(SocketStruct.ByValue s, String opt, UInt64 uint64);

    int nng_socket_set_string(SocketStruct.ByValue s, String opt, String string);

    int nng_socket_set_ptr(SocketStruct.ByValue s, String opt, Pointer pointer);

    int nng_socket_set_ms(SocketStruct.ByValue s, String opt, int duration);

    int nng_socket_id(SocketStruct.ByValue socket);

    // Connection Management - Dialers
    int nng_dialer_close(DialerStruct.ByValue dialer);

    int nng_dialer_create(DialerStruct dialer, SocketStruct.ByValue socket, String url);

    int nng_dialer_id(DialerStruct.ByValue dialer);

    int nng_dialer_start(DialerStruct.ByValue dialer, int flags);

    int nng_dialer_set(DialerStruct.ByValue dialer, String opt, Pointer value, Size size);

    int nng_dialer_set_bool(DialerStruct.ByValue dialer, String opt, boolean value);

    int nng_dialer_set_int(DialerStruct.ByValue dialer, String opt, int value);

    int nng_dialer_set_size(DialerStruct.ByValue dialer, String opt, Size value);

    int nng_dialer_set_uint64(DialerStruct.ByValue dialer, String opt, UInt64 value);

    int nng_dialer_set_string(DialerStruct.ByValue dialer, String opt, String value);

    int nng_dialer_set_ptr(DialerStruct.ByValue dialer, String opt, Pointer value);

    int nng_dialer_set_ms(DialerStruct.ByValue dialer, String opt, int ms);

    int nng_dialer_set_addr(
            DialerStruct.ByValue dialer, String opt, SockAddr sockAddr);

    int nng_dialer_get(DialerStruct.ByValue dialer, String opt, Pointer value, SizeByReference size);

    int nng_dialer_get_bool(DialerStruct.ByValue dialer, String opt, IntByReference bool);

    int nng_dialer_get_int(DialerStruct.ByValue dialer, String opt, IntByReference value);

    int nng_dialer_get_size(DialerStruct.ByValue dialer, String opt, SizeByReference size);

    int nng_dialer_get_uint64(DialerStruct.ByValue dialer, String opt, UInt64ByReference value);

    int nng_dialer_get_string(DialerStruct.ByValue dialer, String opt, Pointer strings);

    int nng_dialer_get_ptr(DialerStruct.ByValue dialer, String opt, PointerByReference pointer);

    int nng_dialer_get_ms(DialerStruct.ByValue dialer, String opt, IntByReference ms);

    int nng_dialer_get_addr(DialerStruct.ByValue dialer, String opt, SockAddr addr);

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

    int nng_pipe_notify(SocketStruct.ByValue socket, int pipeEvent, NngCallback cb, Pointer arg);

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

    int nng_msg_append(MessagePointer msg, Pointer p, Size size);

    int nng_msg_append(MessagePointer msg, Buffer buf, Size size);

    int nng_msg_append(MessagePointer msg, byte[] buf, int size);

    int nng_msg_append_u16(MessagePointer msg, UInt16 val);

    int nng_msg_append_u32(MessagePointer msg, UInt32 val);

    int nng_msg_append_u64(MessagePointer msg, UInt64 val);

    BodyPointer nng_msg_body(MessagePointer msg);

    int nng_msg_chop(MessagePointer msg, Size size);

    int nng_msg_chop_u16(MessagePointer msg, UInt16 size);

    int nng_msg_chop_u32(MessagePointer msg, UInt32 size);

    int nng_msg_chop_u64(MessagePointer msg, UInt64 size);

    void nng_msg_clear(MessagePointer msg);

    int nng_msg_dup(MessageByReference dup, MessagePointer orig);

    void nng_msg_free(MessagePointer msg);

    PipeStruct.ByValue nng_msg_get_pipe(MessagePointer msg);

    int nng_msg_insert(MessagePointer msg, ByteBuffer buf, Size size);

    int nng_msg_insert_u16(MessagePointer msg, UInt16 size);

    int nng_msg_insert_u32(MessagePointer msg, UInt32 size);

    int nng_msg_insert_u64(MessagePointer msg, UInt64 size);

    int nng_msg_len(MessagePointer msg);

    int nng_msg_realloc(MessagePointer msg, Size size);

    int nng_msg_set_pipe(MessagePointer msg, PipeStruct.ByValue pipe);

    int nng_msg_trim(MessagePointer msg, Size size);

    int nng_msg_trim_u16(MessagePointer msg, UInt16ByReference size);

    int nng_msg_trim_u32(MessagePointer msg, UInt32ByReference size);

    int nng_msg_trim_u64(MessagePointer msg, UInt64ByReference size);

    int nng_recvmsg(SocketStruct.ByValue socket, MessageByReference msg, int flags);

    int nng_sendmsg(SocketStruct.ByValue socket, MessagePointer msg, int flags);

    // Message Header Handling
    HeaderPointer nng_msg_header(MessagePointer msg);

    int nng_msg_header_append(MessagePointer msg, ByteBuffer buf, Size size);

    //int nng_msg_header_append(MessagePointer msg, byte[] buf, int size);
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

    int nng_aio_alloc(AioPointerByReference aiop, NngCallback cb, Pointer arg);

    boolean nng_aio_begin(AioPointer aio);

    void nng_aio_cancel(AioPointer aio);

    int nng_aio_count(AioPointer aio);

    void nng_aio_defer(AioPointer aio, NngCallback fn, Pointer arg);

    void nng_aio_finish(AioPointer aio, int err);

    void nng_aio_free(AioPointer aio);

    Pointer nng_aio_get_input(AioPointer aio, int index);

    Pointer nng_aio_get_msg(AioPointer aio);

    Pointer nng_aio_get_output(AioPointer aio, int index);

    int nng_aio_result(AioPointer aio);

    void nng_aio_set_input(AioPointer aio, int index, Pointer param);

//    int nng_aio_set_iov(AioPointer aio, int niov, IovStruct[] iov); // TODO: iov

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

    // Protocol Contexts
    int nng_ctx_close(ContextStruct.ByValue context);

    int nng_ctx_id(ContextStruct.ByValue context);

    int nng_ctx_open(ContextStruct context, SocketStruct.ByValue socket);

    void nng_ctx_recv(ContextStruct.ByValue context, AioPointer aio);

    void nng_ctx_send(ContextStruct.ByValue context, AioPointer aio);

    int nng_ctx_get(ContextStruct.ByValue s, String opt, Pointer val, SizeByReference size);

    int nng_ctx_get_bool(ContextStruct.ByValue s, String opt, IntByReference bool);

    int nng_ctx_get_int(ContextStruct.ByValue s, String opt, IntByReference intRef);

    int nng_ctx_get_size(ContextStruct.ByValue s, String opt, SizeByReference size);

    int nng_ctx_get_uint64(ContextStruct.ByValue s, String opt, UInt64ByReference uint64);

    int nng_ctx_get_string(ContextStruct.ByValue s, String opt, Pointer strings);

    int nng_ctx_get_ptr(ContextStruct.ByValue s, String opt, Pointer pointer);

    int nng_ctx_get_ms(ContextStruct.ByValue s, String opt, IntByReference duration);

    int nng_ctx_get_addr(ContextStruct.ByValue s, String opt, SockAddr addr);

    int nng_ctx_set(ContextStruct.ByValue s, String opt, Pointer val, Size size);

    int nng_ctx_set_bool(ContextStruct.ByValue s, String opt, boolean bool);

    int nng_ctx_set_int(ContextStruct.ByValue s, String opt, int intRef);

    int nng_ctx_set_size(ContextStruct.ByValue s, String opt, Size size);

    int nng_ctx_set_uint64(ContextStruct.ByValue s, String opt, UInt64 uint64);

    int nng_ctx_set_string(ContextStruct.ByValue s, String opt, String string);

    int nng_ctx_set_ptr(ContextStruct.ByValue s, String opt, Pointer pointer);

    int nng_ctx_set_ms(ContextStruct.ByValue s, String opt, int duration);

    // URL Object
    // TODO: URL Object
    int nng_url_parse(UrlByReference urlPointer, String rawUrl);

    void nng_url_free(UrlStruct url);

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

    void nng_http_req_free(HttpReqPointer req);

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

    void nng_http_res_free(HttpResPointer res);

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

    void nng_http_client_free(HttpClientPointer client);

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
//
//    int nng_tls_config_alloc(TlsConfigByReference ref, int mode);
//
//    int nng_tls_config_auth_mode(TlsConfigPointer cfg, int mode);
//
//    int nng_tls_config_ca_chain(TlsConfigPointer cfg, String chain, String crl);
//
//    int nng_tls_config_ca_file(TlsConfigPointer cfg, String path);
//
//    int nng_tls_config_cert_key_file(TlsConfigPointer cfg, String pathToKey, String password);
//
//    int nng_tls_config_free(TlsConfigPointer cfg);
//
//    int nng_tls_config_hold(TlsConfigPointer cfg);
//
//    int nng_tls_config_own_cert(TlsConfigPointer cfg, String cert, String key, String password);
//
//    int nng_tls_config_server_name(TlsConfigPointer cfg, String serverName);
//
//    int nng_tls_config_version(TlsConfigPointer cfg, int minVersion, int maxVersion);
//
//    String nng_tls_engine_description();
//
//    boolean nng_tls_engine_fips_mode();
//
//    String nng_tls_engine_name();

    // mqtt
    int nng_mqtt_msg_alloc(MessageByReference msgRef, Size size);

    int nng_mqtt_msg_proto_data_alloc(MessagePointer msg);

    void nng_mqtt_msg_proto_data_free(MessagePointer msg);

    int nng_mqtt_msg_encode(MessagePointer msg);

    int nng_mqtt_msg_decode(MessagePointer msg);

    /**
     * @param msg        nng_msg of mqtt
     * @param packetType {@link io.sisu.nng.internal.mqtt.constants.MqttPacketType}
     */
    void nng_mqtt_msg_set_packet_type(MessagePointer msg, byte packetType);

    byte nng_mqtt_msg_get_packet_type(MessagePointer msg);

    //
    void nng_mqtt_msg_set_connect_proto_version(MessagePointer msg, byte version);

    void nng_mqtt_msg_set_connect_keep_alive(MessagePointer msg, UInt16 keepAlive);

    void nng_mqtt_msg_set_connect_client_id(MessagePointer msg, String clientId);

    void nng_mqtt_msg_set_connect_user_name(MessagePointer msg, String username);

    void nng_mqtt_msg_set_connect_password(MessagePointer msg, String password);

    void nng_mqtt_msg_set_connect_clean_session(MessagePointer msg, boolean cleanSession);

    void nng_mqtt_msg_set_connect_will_topic(MessagePointer msg, String topic);

    void nng_mqtt_msg_set_connect_will_msg(MessagePointer msg, byte[] data, UInt32 len);

    void nng_mqtt_msg_set_connect_will_msg(MessagePointer msg, Pointer data, UInt32 len);

    void nng_mqtt_msg_set_connect_will_msg(MessagePointer msg, Buffer data, UInt32 len);

    void nng_mqtt_msg_set_connect_will_retain(MessagePointer msg, boolean retain);

    void nng_mqtt_msg_set_connect_will_qos(MessagePointer msg, byte qos);

    void nng_mqtt_msg_set_connect_property(MessagePointer msg, PropertyPointer property);

    String nng_mqtt_msg_get_connect_user_name(MessagePointer msg);

    String nng_mqtt_msg_get_connect_password(MessagePointer msg);

    boolean nng_mqtt_msg_get_connect_clean_session(MessagePointer msg);

    byte nng_mqtt_msg_get_connect_proto_version(MessagePointer msg);

    UInt16 nng_mqtt_msg_get_connect_keep_alive(MessagePointer msg);

    String nng_mqtt_msg_get_connect_client_id(MessagePointer msg);

    String nng_mqtt_msg_get_connect_will_topic(MessagePointer msg);

    BytesPointer nng_mqtt_msg_get_connect_will_msg(MessagePointer msg, UInt32ByReference len);

    boolean nng_mqtt_msg_get_connect_will_retain(MessagePointer msg);

    byte nng_mqtt_msg_get_connect_will_qos(MessagePointer msg);

    PropertyPointer nng_mqtt_msg_get_connect_property(MessagePointer msg);

    byte nng_mqtt_msg_get_connack_return_code(MessagePointer msg);

    byte nng_mqtt_msg_get_connack_flags(MessagePointer msg);

    PropertyPointer nng_mqtt_msg_get_connack_property(MessagePointer msg);

    void nng_mqtt_msg_set_publish_qos(MessagePointer msg, byte qos);

    byte nng_mqtt_msg_get_publish_qos(MessagePointer msg);

    void nng_mqtt_msg_set_publish_retain(MessagePointer msg, boolean retain);

    boolean nng_mqtt_msg_get_publish_retain(MessagePointer msg);

    void nng_mqtt_msg_set_publish_dup(MessagePointer msg, boolean dup);

    boolean nng_mqtt_msg_get_publish_dup(MessagePointer msg);

    int nng_mqtt_msg_set_publish_topic(MessagePointer msg, String topic);

    String nng_mqtt_msg_get_publish_topic(MessagePointer msg, UInt32ByReference len);

    void nng_mqtt_msg_set_publish_payload(MessagePointer msg, byte[] payload, UInt32 len);

    void nng_mqtt_msg_set_publish_payload(MessagePointer msg, ByteBuffer payload, UInt32 len);

    BytesPointer nng_mqtt_msg_get_publish_payload(MessagePointer msg, UInt32ByReference len);

    PropertyPointer nng_mqtt_msg_get_publish_property(MessagePointer msg);

    void nng_mqtt_msg_set_publish_property(MessagePointer msg, PropertyPointer property);

    TopicQosPointer nng_mqtt_msg_get_subscribe_topics(MessagePointer msg, UInt32ByReference count);

    void nng_mqtt_msg_set_subscribe_topics(MessagePointer msg, TopicQosPointer topicQos, UInt32 count);

    void nng_mqtt_msg_set_suback_return_codes(MessagePointer msg, byte[] codec, UInt32 count);

    void nng_mqtt_msg_set_suback_return_codes(MessagePointer msg, Pointer codec, UInt32 count);

    void nng_mqtt_msg_set_suback_return_codes(MessagePointer msg, ByteBuffer codec, UInt32 count);

    PropertyPointer nng_mqtt_msg_get_subscribe_property(MessagePointer msg);

    void nng_mqtt_msg_set_subscribe_property(MessagePointer msg, PropertyPointer property);

    BytesPointer nng_mqtt_msg_get_suback_return_codes(MessagePointer msg, UInt32ByReference count);

    PropertyPointer nng_mqtt_msg_get_suback_property(MessagePointer msg);

    void nng_mqtt_msg_set_suback_property(MessagePointer msg, PropertyPointer property);

    void nng_mqtt_msg_set_unsubscribe_topics(MessagePointer msg, TopicPointer topics, UInt32 count);

    TopicPointer nng_mqtt_msg_get_unsubscribe_topics(MessagePointer msg, UInt32ByReference count);

    PropertyPointer nng_mqtt_msg_get_unsubscribe_property(MessagePointer msg);

    void nng_mqtt_msg_set_unsubscribe_property(MessagePointer msg, PropertyPointer property);

    PropertyPointer nng_mqtt_msg_get_disconnect_property(MessagePointer msg);

    void nng_mqtt_msg_set_disconnect_property(MessagePointer msg, PropertyPointer property);

    TopicPointer nng_mqtt_topic_array_create(Size count);

    void nng_mqtt_topic_array_set(TopicPointer topicList, Size index, String topic);

    void nng_mqtt_topic_array_free(TopicPointer topicList, Size count);

    TopicQosPointer nng_mqtt_topic_qos_array_create(Size count);

    void nng_mqtt_topic_qos_array_set(
            TopicQosPointer topicQosList, Size index, String topic, byte qos);

    void nng_mqtt_topic_qos_array_free(TopicQosPointer topicQosList, Size count);

    int nng_mqtt_set_connect_cb(SocketStruct socket, NngCallback cb, Pointer arg);

    int nng_mqtt_set_disconnect_cb(SocketStruct socket, NngCallback cb, Pointer arg);

    void nng_mqtt_msg_set_disconnect_reason_code(MessagePointer msgmsg, byte reason_code);

    UInt32 get_mqtt_properties_len(PropertyPointer property);

    int mqtt_property_free(PropertyPointer property);

    void mqtt_property_foreach(PropertyPointer property, NngCallback cb);

    int mqtt_property_dup(PropertyPointerByReference dup, PropertyPointer src);

    PropertyPointer mqtt_property_pub_by_will(PropertyPointer willProperty);

    PropertyPointer mqtt_property_alloc();

    PropertyPointer mqtt_property_set_value_u8(byte prop_id, byte value);

    PropertyPointer mqtt_property_set_value_u16(byte prop_id, UInt16 value);

    PropertyPointer mqtt_property_set_value_u32(byte prop_id, UInt32 value);

    PropertyPointer mqtt_property_set_value_varint(byte prop_id, UInt32 value);

    PropertyPointer mqtt_property_set_value_binary(byte prop_id, byte[] value, UInt32 len, boolean copy_value);

    PropertyPointer mqtt_property_set_value_binary(byte prop_id, ByteBuffer value, UInt32 len, boolean copy_value);

    PropertyPointer mqtt_property_set_value_binary(byte prop_id, Pointer value, UInt32 len, boolean copy_value);

    PropertyPointer mqtt_property_set_value_str(byte prop_id, String value, UInt32 len, boolean copy_value);

    PropertyPointer mqtt_property_set_value_strpair(byte prop_id, String key, UInt32 key_len, String value, UInt32 value_len, boolean copy_value);

    /**
     * @param prop_id property id
     * @return {@link io.sisu.nng.internal.mqtt.constants.PropertyType}
     */
    byte mqtt_property_get_value_type(byte prop_id);
//
//    //TODO
////    property_data *
////
//    mqtt_property_get_value(PropertyPointer propertyprop, uint8_t prop_id);

    void mqtt_property_append(PropertyPointer propertyList, PropertyPointer last);

    //    // Note that MQTT sockets can be connected to at most a single server.
//// Creating the client does not connect it.
    int nng_mqtt_client_open(SocketStruct socket);

    int nng_mqttv5_client_open(SocketStruct socket);

    int nng_mqtt_quic_client_open(SocketStruct socket, String url);

    int nng_mqtt_quic_set_connect_cb(SocketStruct socket, NngMsgCallback callback, Pointer arg);

    int nng_mqtt_quic_set_disconnect_cb(SocketStruct socket, NngMsgCallback callback, Pointer arg);

    int nng_mqtt_quic_set_msg_recv_cb(SocketStruct socket, NngMsgCallback callback, Pointer arg);

    int nng_mqtt_quic_set_msg_send_cb(SocketStruct socket, NngMsgCallback callback, Pointer arg);

    ////    nng_mqtt_client *
////
////    nng_mqtt_client_alloc(SocketStruct socket, nng_mqtt_cb_opt*, bool);
//
////    void nng_mqtt_client_free(nng_mqtt_client*, bool);
//
    int nng_mqtt_subscribe(SocketStruct socket, TopicQosPointer topicQos, Size count, PropertyPointer property);

//    int nng_mqtt_subscribe_async(nng_mqtt_client *, TopicQosPointer topicQos, Size count, PropertyPointer property);

    int nng_mqtt_unsubscribe(SocketStruct socket, TopicQosPointer topicQos, Size count, PropertyPointer property);

//    int nng_mqtt_unsubscribe_async(nng_mqtt_client *, TopicQosPointer topicQos, Size count, PropertyPointer property);

    int nng_mqtt_disconnect(SocketStruct socket, byte code, PropertyPointer property);


    // XXX Memtrack...requires patch for NNG to track memory allocation
    // int nng_memtrack(UInt64ByReference alloc, UInt64ByReference freed);
}
