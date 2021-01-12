package io.sisu.nng;

import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class Example {
    public static void main(String[] argv) throws NngException {
        final String url = "inproc://example";
        Socket req = new Req0Socket();
        Socket rep = new Rep0Socket();

        rep.listen(url);
        req.dial(url);

        Message msg = new Message();
        msg.append("hey man".getBytes(StandardCharsets.UTF_8));
        req.sendMessage(msg);

        // After sending, we no longer own the Message
        assert(!msg.isValid());

        Message msg2 = rep.receiveMessage();
        assert(msg2.isValid());

        String msg2Str = Charset.defaultCharset()
                .decode(msg2.getBody()).toString();
        assert("hey man".equalsIgnoreCase(msg2Str));
        System.out.println("Rep socket heard: " + msg2Str);
    }
}
