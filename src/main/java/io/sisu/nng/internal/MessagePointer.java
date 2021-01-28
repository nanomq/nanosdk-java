package io.sisu.nng.internal;

import com.sun.jna.FromNativeContext;
import com.sun.jna.PointerType;

public class MessagePointer extends PointerType {
    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return super.fromNative(nativeValue, context);
    }
}
