package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import io.sisu.nng.internal.jna.UInt16;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.jna.UInt64;

import java.util.Arrays;

/**
 * Socket Address Union
 *
 * TODO: fix this Uint16 convert nonsense...seems like we should need to use this goofy logic if things are working.
 */
public class SockAddr extends Union {
    public UInt16 s_family;
    public Local s_ipc;
    public Local s_inproc;
    public Inet s_in;
    public Inet6 s_in6;
    public ZeroTier s_zt;

    public Local readAsIpc() {
        return (Local) getTypedValue(Local.class);
    }

    public Local readAsInproc() {
        return (Local) getTypedValue(Local.class);
    }

    public Inet readAsInet() {
        return (Inet) getTypedValue(Inet.class);
    }

    public Inet6 readAsInet6() {
        return (Inet6) getTypedValue(Inet6.class);
    }

    public ZeroTier readAsZeroTier() {
        return (ZeroTier) getTypedValue(ZeroTier.class);
    }

    @FieldOrder({"sa_family", "sa_path"})
    public static class Local extends Structure {
        public UInt16 sa_family;
        public byte[] sa_path;

        public Local() {
            sa_family = new UInt16();
            sa_path = new byte[256];
        }

        @Override
        public String toString() {
            return "SockAddrLocal{" +
                    "sa_family=" + sa_family.convert() +
                    ", sa_path=" + getPath() +
                    '}';
        }

        public String getPath() {
            return Native.toString(sa_path);
        }
    }

    @FieldOrder({"sa_family", "sa_port", "sa_addr"})
    public static class Inet extends Structure {
        public UInt16 sa_family;
        public UInt16 sa_port;
        public UInt32 sa_addr;
        public Inet() {}

        @Override
        public String toString() {
            return "SockAddrInet{" +
                    "sa_family=" + sa_family.convert() +
                    ", sa_port=" + sa_port.convert() +
                    ", sa_addr=" + sa_addr.convert() +
                    '}';
        }
    }

    @FieldOrder({"sa_family", "sa_port", "sa_addr"})
    public static class Inet6 extends Structure {
        public UInt16 sa_family;
        public UInt16 sa_port;
        public byte[] sa_addr = new byte[16];

        public Inet6() {}

        @Override
        public String toString() {
            return "SockAddrInet6{" +
                    "sa_family=" + sa_family.convert() +
                    ", sa_port=" + sa_port.convert() +
                    ", sa_addr=" + Arrays.toString(sa_addr) +
                    '}';
        }
    }

    @FieldOrder({"sa_family", "sa_nwid", "sa_nodeid", "sa_port"})
    public static class ZeroTier extends Structure {
        public UInt16 sa_family;
        public UInt64 sa_nwid;
        public UInt64 sa_nodeid;
        public UInt32 sa_port;
        public ZeroTier() {}

        @Override
        public String toString() {
            return "SockAddrZeroTier{" +
                    "sa_family=" + sa_family +
                    ", sa_nwid=" + sa_nwid +
                    ", sa_nodeid=" + sa_nodeid +
                    ", sa_port=" + sa_port +
                    '}';
        }
    }

    public enum Family {
        NNG_AF_UNSPEC(0),
        NNG_AF_INPROC(1),
        NNG_AF_IPC(2),
        NNG_AF_INET(3),
        NNG_AF_INET6(4),
        NNG_AF_ZT(5),
        UNKNOWN(-1);

        private final int family;

        private Family(int family) {
            this.family = family;
        }

        public static Family getFamily(int value) {
            // XXX: I'm lazy for now
            return Arrays.stream(values())
                    .filter(item -> item.family == value)
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
