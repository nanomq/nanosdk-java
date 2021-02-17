package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.TlsConfigByReference;
import io.sisu.nng.internal.TlsConfigPointer;
import io.sisu.nng.internal.jna.UInt16;

import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * A configuration for TLS-enabled transport.
 * <p>
 * Given nng_tls_config objects are meant to be shared and reused for multiple sockets, we'll need
 * a design to deal with the fact nng does ref counting for these objects.
 */
public class TlsConfig {
    private TlsConfigPointer tls;
    private SocketMode socketMode;

    public enum SocketMode {
        CLIENT(0),
        SERVER(1);
        private int value;

        SocketMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum AuthMode {
        NONE(0),
        OPTIONAL(1),
        REQUIRED(2);

        private int value;

        AuthMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum TlsVersion {
        TLS_1_0(new UInt16(0x301)),
        TLS_1_1(new UInt16(0x302)),
        TLS_1_2(new UInt16(0x303)),
        TLS_1_3(new UInt16(0x304));

        private UInt16 value;

        TlsVersion(UInt16 value) {
            this.value = value;
        }

        public UInt16 getValue() {
            return this.value;
        }
    }

    public TlsConfig(SocketMode mode) throws NngException {
        TlsConfigByReference ref = new TlsConfigByReference();
        int rv = Nng.lib().nng_tls_config_alloc(ref, mode.getValue());
        if (rv != 0) {
            // XXX: only issue should be memory related. Throw for now.
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        this.tls = ref.getTlsConfig();
        this.socketMode = mode;
    }

    void setAuthMode(AuthMode authMode) throws NngException {
        int rv = Nng.lib().nng_tls_config_auth_mode(this.tls, authMode.value);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    void setCertificate(String cert, String key) throws NngException {
        setCertificate(cert, key, null);
    }

    void setCertificate(String cert, String key, String password) throws NngException {
        int rv = Nng.lib().nng_tls_config_own_cert(this.tls, cert, key, password);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    void setPrivateKey(Path keyFile, String password) throws NngException {
        int rv = Nng.lib().nng_tls_config_cert_key_file(this.tls, keyFile.toString(), password);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    void setServerName(String name) throws NngException {
        int rv = Nng.lib().nng_tls_config_server_name(this.tls, name);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    protected Pointer getPointer() {
        return this.tls.getPointer();
    }

}
