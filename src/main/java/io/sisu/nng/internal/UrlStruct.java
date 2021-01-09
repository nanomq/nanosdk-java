package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({
        "u_rawurl",
        "u_scheme",
        "u_userinfo",
        "u_host",
        "u_hostname",
        "u_port",
        "u_path",
        "u_query",
        "u_fragment",
        "u_requri",
})
public class UrlStruct extends Structure {
    public NativeStringPointer u_rawurl;   // never NULL
    public NativeStringPointer u_scheme;   // never NULL
    public NativeStringPointer u_userinfo; // will be NULL if not specified
    public NativeStringPointer u_host;     // including colon and port
    public NativeStringPointer u_hostname; // name only, will be "" if not specified
    public NativeStringPointer u_port;     // port, will be "" if not specified
    public NativeStringPointer u_path;     // path, will be "" if not specified
    public NativeStringPointer u_query;    // without '?', will be NULL if not specified
    public NativeStringPointer u_fragment; // without '#', will be NULL if not specified
    public NativeStringPointer u_requri;   // includes query and fragment, "" if not specified

    public UrlStruct() {}

    public UrlStruct(Pointer p) {
        useMemory(p);
        if (p != Pointer.NULL) {
            u_rawurl = new NativeStringPointer(p.getPointer(0 * Native.POINTER_SIZE));
            u_scheme = new NativeStringPointer(p.getPointer(1 * Native.POINTER_SIZE));
            u_userinfo = new NativeStringPointer(p.getPointer(2 * Native.POINTER_SIZE));
            u_host = new NativeStringPointer(p.getPointer(3 * Native.POINTER_SIZE));
            u_hostname = new NativeStringPointer(p.getPointer(4 * Native.POINTER_SIZE));
            u_port = new NativeStringPointer(p.getPointer(5 * Native.POINTER_SIZE));
            u_path = new NativeStringPointer(p.getPointer(6 * Native.POINTER_SIZE));
            u_query = new NativeStringPointer(p.getPointer(7 * Native.POINTER_SIZE));
            u_fragment = new NativeStringPointer(p.getPointer(8 * Native.POINTER_SIZE));
            u_requri = new NativeStringPointer(p.getPointer(9 * Native.POINTER_SIZE));
        }
    }
}
