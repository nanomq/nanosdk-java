package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    //@Disabled("Requires a HTTP server, so this is only available for manual testing")
    public void HttpClientTest() {
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

        //System.out.println(StandardCharsets.UTF_8.decode(iov.iov_buf).toString());
        //System.out.println(StandardCharsets.UTF_8.decode(array.getIov(0).iov_buf).toString());
        //System.out.println(StandardCharsets.UTF_8.decode(array.getIov(1).iov_buf).toString());
        // Free things
        assertOk(Nng.lib().nng_http_res_free(res));
        assertOk(Nng.lib().nng_http_req_free(req));
        assertOk(Nng.lib().nng_http_client_free(client));
    }
}
