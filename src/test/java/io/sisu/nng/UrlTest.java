package io.sisu.nng;

import io.sisu.nng.internal.UrlByReference;
import io.sisu.nng.internal.UrlStruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlTest {
    @Test
    public void createAndDestroyTest() {
        final String rawUrl = "http://dave:stuff@localhost/ok";
        UrlByReference p = new UrlByReference();
        int rv = Nng.lib().nng_url_parse(p, rawUrl);
        if (rv != 0) {
            Assertions.fail(Nng.lib().nng_strerror(rv));
        }
        UrlStruct url = p.getUrl();
        Assertions.assertEquals(rawUrl, url.u_rawurl.toString());
        Assertions.assertEquals("80", url.u_port.toString());
        Assertions.assertEquals(null, url.u_fragment.toString());

        rv = Nng.lib().nng_url_free(url);
        if (rv != 0) {
            Assertions.fail(Nng.lib().nng_strerror(rv));
        }
    }
}
