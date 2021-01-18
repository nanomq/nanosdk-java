package io.sisu.nng;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.sisu.nng.internal.*;
import io.sisu.nng.internal.jna.Size;
import io.sisu.nng.internal.jna.SizeByReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HttpTest {

    public void assertOk(int rv) {
        if (rv != 0) {
            Assertions.fail(Nng.lib().nng_strerror(rv));
        }
    }

    public void wait(AioPointer aio) {
        Nng.lib().nng_aio_wait(aio);
        int rv = Nng.lib().nng_aio_result(aio);
        if (rv != 0) {
            Assertions.fail(Nng.lib().nng_strerror(rv));
        }
    }

    @Test
    public void httpIntegrationTest() {
        final String hello = "Hello there! How are you?";
        UrlByReference urlRef = new UrlByReference();
        assertOk(Nng.lib().nng_url_parse(urlRef, "http://localhost:9999/hello"));
        UrlStruct url = urlRef.getUrl();

        // Start the server
        HttpServerPointerByReference serverRef = new HttpServerPointerByReference();
        assertOk(Nng.lib().nng_http_server_hold(serverRef, url));
        HttpServerPointer server = serverRef.getHttpServerPointer();

        byte[] raw = hello.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocateDirect(raw.length);
        buffer.put(raw);
        HttpHandlerPointerByReference handlerRef = new HttpHandlerPointerByReference();
        assertOk(Nng.lib().nng_http_handler_alloc_static(handlerRef, url.u_path.toString(),
                Native.getDirectBufferPointer(buffer), new Size(buffer.limit()), "text/html; charset=UTF-8"));
        HttpHandlerPointer handler = handlerRef.getHandlerPointer();
        assertOk(Nng.lib().nng_http_server_add_handler(server, handler));
        assertOk(Nng.lib().nng_http_server_start(server));

        // Validate using a client
        HttpReqPointerByReference reqRef = new HttpReqPointerByReference();
        assertOk(Nng.lib().nng_http_req_alloc(reqRef, url));
        HttpReqPointer req = reqRef.getHttpReqPointer();

        HttpResPointerByReference resRef = new HttpResPointerByReference();
        assertOk(Nng.lib().nng_http_res_alloc(resRef));
        HttpResPointer res = resRef.getHttpReqPointer();

        HttpClientPointerByReference clientRef = new HttpClientPointerByReference();
        assertOk(Nng.lib().nng_http_client_alloc(clientRef, url));
        HttpClientPointer client = clientRef.getHttpClientPointer();

        AioPointerByReference aioRef = new AioPointerByReference();
        assertOk(Nng.lib().nng_aio_alloc(aioRef, null, null));
        AioPointer aio = aioRef.getAioPointer();
        Nng.lib().nng_aio_set_timeout(aio, 1000);

        // Connect
        Nng.lib().nng_http_client_connect(client, aio);
        wait(aio);

        // Get connection
        Pointer conn = Nng.lib().nng_aio_get_output(aio, 0);

        // Write request
        Nng.lib().nng_http_conn_write_req(conn, req, aio);
        wait(aio);

        // Read response
        Nng.lib().nng_http_conn_read_res(conn, res, aio);
        wait(aio);

        // Get status
        short status = Nng.lib().nng_http_res_get_status(res);
        Assertions.assertEquals(200, status);
        System.out.println("Status: " + status);

        // Try getting a header
        String contentLength = Nng.lib().nng_http_res_get_header(res, "Content-Length");
        Assertions.assertFalse(contentLength.isEmpty());
        int bodyLen = Integer.parseInt(contentLength);
        System.out.println("Content-Length: " + bodyLen);

        // Try getting the body
        IovStruct[] array = IovStruct.allocate(new int[]{bodyLen});
        assertOk(Nng.lib().nng_aio_set_iov(aio, array.length, array));
        Nng.lib().nng_http_conn_read(conn, aio);
        wait(aio);

        String result = StandardCharsets.UTF_8.decode(array[0].iov_buf).toString();
        Assertions.assertEquals(hello, result);

        Nng.lib().nng_http_res_free(res);
        Nng.lib().nng_http_req_free(req);
        Nng.lib().nng_http_client_free(client);

        Nng.lib().nng_http_server_stop(server);
        Nng.lib().nng_http_server_release(server);
    }

    @Test
    public void resBodyTest() {
        HttpResPointerByReference resRef = new HttpResPointerByReference();
        assertOk(Nng.lib().nng_http_res_alloc(resRef));
        HttpResPointer res = resRef.getHttpReqPointer();

        final String msg = "testing 123";
        final byte[] rawMsg = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocateDirect(rawMsg.length);
        Native.getDirectBufferPointer(buf).setString(0, "testing");
        assertOk(Nng.lib().nng_http_res_set_data(res, buf, new Size(buf.limit())));

        BodyPointerByReference bodyRef = new BodyPointerByReference();
        SizeByReference sizeRef = new SizeByReference();
        Nng.lib().nng_http_res_get_data(res, bodyRef, sizeRef);

        Assertions.assertEquals(rawMsg.length, sizeRef.getSize().convert());

        BodyPointer body = bodyRef.getBodyPointer();
        System.out.println(body.getPointer().getString(0));
    }

    @Test
    @Disabled("Requires a HTTP server, so this is only available for manual testing")
    public void httpClientTest() {
        UrlByReference urlRef = new UrlByReference();
        assertOk(Nng.lib().nng_url_parse(urlRef, "http://localhost:8888/"));
        UrlStruct url = urlRef.getUrl();

        HttpReqPointerByReference reqRef = new HttpReqPointerByReference();
        assertOk(Nng.lib().nng_http_req_alloc(reqRef, url));
        HttpReqPointer req = reqRef.getHttpReqPointer();

        HttpResPointerByReference resRef = new HttpResPointerByReference();
        assertOk(Nng.lib().nng_http_res_alloc(resRef));
        HttpResPointer res = resRef.getHttpReqPointer();

        HttpClientPointerByReference clientRef = new HttpClientPointerByReference();
        assertOk(Nng.lib().nng_http_client_alloc(clientRef, url));
        HttpClientPointer client = clientRef.getHttpClientPointer();

        AioPointerByReference aioRef = new AioPointerByReference();
        assertOk(Nng.lib().nng_aio_alloc(aioRef, null, null));
        AioPointer aio = aioRef.getAioPointer();
        Nng.lib().nng_aio_set_timeout(aio, 1000);

        // Connect
        Nng.lib().nng_http_client_connect(client, aio);
        wait(aio);

        // Get connection
        Pointer conn = Nng.lib().nng_aio_get_output(aio, 0);

        // Write request
        Nng.lib().nng_http_conn_write_req(conn, req, aio);
        wait(aio);

        // Read response
        Nng.lib().nng_http_conn_read_res(conn, res, aio);
        wait(aio);

        // Get status
        short status = Nng.lib().nng_http_res_get_status(res);
        Assertions.assertEquals(200, status);
        System.out.println("Status: " + status);

        // Try getting a header
        String contentLength = Nng.lib().nng_http_res_get_header(res, "Content-Length");
        Assertions.assertFalse(contentLength.isEmpty());
        int bodyLen = Integer.parseInt(contentLength);
        System.out.println("Content-Length: " + bodyLen);

        String server = Nng.lib().nng_http_res_get_header(res, "Server");
        System.out.println("Server: " + server);

        // Try getting the body
        IovStruct[] array = IovStruct.allocate(128, 128, 128);

        assertOk(Nng.lib().nng_aio_set_iov(aio, array.length, array));
        Nng.lib().nng_http_conn_read(conn, aio);
        wait(aio);

        for (IovStruct iov : array) {
            System.out.println(iov);
        }
        Nng.lib().nng_http_res_free(res);
        Nng.lib().nng_http_req_free(req);
        Nng.lib().nng_http_client_free(client);
    }

    @Test
    @Disabled
    public void httpServerTest() {
        UrlByReference urlRef = new UrlByReference();
        assertOk(Nng.lib().nng_url_parse(urlRef, "http://0.0.0.0:9999"));
        UrlStruct url = urlRef.getUrl();

        HttpServerPointerByReference serverRef = new HttpServerPointerByReference();
        assertOk(Nng.lib().nng_http_server_hold(serverRef, url));

        HttpServerPointer server = serverRef.getHttpServerPointer();

        byte[] raw = "<html>Hey man</html>".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocateDirect(raw.length);
        buffer.put(raw);
        HttpHandlerPointerByReference handlerRef = new HttpHandlerPointerByReference();
        assertOk(Nng.lib().nng_http_handler_alloc_static(handlerRef, "/hello",
                Native.getDirectBufferPointer(buffer), new Size(buffer.limit()), "text/html; charset=UTF-8"));
        HttpHandlerPointer handler = handlerRef.getHandlerPointer();
        assertOk(Nng.lib().nng_http_server_add_handler(server, handler));
        assertOk(Nng.lib().nng_http_server_start(server));

        SockAddr addr = new SockAddr();
        assertOk(Nng.lib().nng_http_server_get_addr(server, addr));
        SockAddr.Inet inet = addr.readAsInet();
        Assertions.assertEquals(Integer.parseInt(url.u_port.getPointer().getString(0)),
                inet.sa_port.convert());

        AioPointerByReference aioRef = new AioPointerByReference();
        assertOk(Nng.lib().nng_aio_alloc(aioRef, null, null));
        AioPointer aio = aioRef.getAioPointer();
        Nng.lib().nng_sleep_aio(1000, aio);
        wait(aio);
        Nng.lib().nng_http_server_release(server);
    }
}
