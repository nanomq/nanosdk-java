package io.sisu.nng;

import com.sun.jna.ptr.IntByReference;
import io.sisu.nng.internal.jna.UInt64ByReference;

public class Memtrack {
    public static void main(String argv[]) throws NngException {
        UInt64ByReference alloc = new UInt64ByReference();
        UInt64ByReference freed = new UInt64ByReference();
        int rv = 0;
        rv = Nng.lib().nng_memtrack(alloc, freed);

        System.out.println("rv: "+ rv);

        Message message = new Message(1);
        message.appendU64(1);
        message.free();

        rv = Nng.lib().nng_memtrack(alloc, freed);

        System.out.println("rv: "+ rv);
        System.out.println(String.format("alloc: %d, freed: %d",
                alloc.getUInt64().longValue(),
                freed.getUInt64().longValue()));
    }
}
