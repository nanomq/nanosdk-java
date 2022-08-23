package io.sisu.nng;

import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class TlsTest {
//    public static final String certFilename = "/cert.crt";
//    public static final String keyFilename = "/key.key";
//
//    public static String cert = "";
//    public static String key = "";
//    @BeforeAll
//    public static void setup() throws Exception {
//        CharBuffer buf = CharBuffer.allocate(16 * 1024);
//        try (InputStreamReader isr =
//                     new InputStreamReader(TlsTest.class.getResourceAsStream(certFilename))) {
//            isr.read(buf);
//            buf.flip();
//            cert = buf.toString();
//        }
//        buf.clear();
//        try (InputStreamReader isr =
//                     new InputStreamReader(TlsTest.class.getResourceAsStream(keyFilename))) {
//            isr.read(buf);
//            buf.flip();
//            key = buf.toString();
//        }
//    }
//
//
//    @Test
//    public void simpleTlsSetupTest() throws NngException {
//        Socket client = new Req0Socket();
//        Socket server = new Rep0Socket();
//
//        TlsConfig clientConfig = new TlsConfig(TlsConfig.SocketMode.CLIENT);
//        clientConfig.setAuthMode(TlsConfig.AuthMode.NONE);
//        // clientConfig.setServerName("localhost");
//        client.setTlsConfig(clientConfig);
//
//        TlsConfig serverConfig = new TlsConfig(TlsConfig.SocketMode.SERVER);
//        serverConfig.setCertificate(cert, key);
//        server.setTlsConfig(serverConfig);
//
//        server.listen("tls+tcp://localhost:9999");
//        client.dial("tls+tcp://localhost:9999");
//
//        Message msg = new Message();
//        msg.append("hey!");
//        client.sendMessage(msg);
//
//        Message msg2 = server.receiveMessage();
//        Assertions.assertEquals("hey!",
//                Charset.defaultCharset().decode(msg2.getBody()).toString());
//        msg.free();
//    }
}
