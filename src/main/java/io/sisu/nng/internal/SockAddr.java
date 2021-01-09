package io.sisu.nng.internal;

import com.sun.jna.Structure;
import com.sun.jna.Union;

import java.util.Arrays;

public class SockAddr extends Union {
    public SockAddrLocal s_ipc;
    public SockAddrLocal s_inproc;
    public SockAddrInet s_in;
    public SockAddrInet s_in6;
    public SockAddrZeroTier s_zt;


    @FieldOrder({"sa_family", "sa_path"})
    public static class SockAddrLocal extends Structure {
        public short sa_family;
        public short[] sa_path;
        public SockAddrLocal() {
            sa_family = 0;
            sa_path = new short[128];
        }

        @Override
        public String toString() {
            return "SockAddrLocal{" +
                    "sa_family=" + sa_family +
                    ", sa_path=" + Arrays.toString(sa_path) +
                    '}';
        }
    }

    @FieldOrder({"sa_family", "sa_port", "sa_addr"})
    public static class SockAddrInet extends Structure {
        public short sa_family;
        public short sa_port;
        public int sa_addr;
        public SockAddrInet() {}

        @Override
        public String toString() {
            return "SockAddrInet{" +
                    "sa_family=" + sa_family +
                    ", sa_port=" + sa_port +
                    ", sa_addr=" + sa_addr +
                    '}';
        }
    }

    @FieldOrder({"sa_family", "sa_port", "sa_addr"})
    public static class SockAddrInet6 extends Structure {
        public short sa_family;
        public short sa_port;
        public char[] sa_addr = new char[16];

        public SockAddrInet6() {}

        @Override
        public String toString() {
            return "SockAddrInet6{" +
                    "sa_family=" + sa_family +
                    ", sa_port=" + sa_port +
                    ", sa_addr=" + Arrays.toString(sa_addr) +
                    '}';
        }
    }

    @FieldOrder({"sa_family", "sa_nwid", "sa_nodeid", "sa_port"})
    public static class SockAddrZeroTier extends Structure {
        public short sa_family;
        public long sa_nwid;
        public long sa_nodeid;
        public int sa_port;
        public SockAddrZeroTier() {}

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
}
