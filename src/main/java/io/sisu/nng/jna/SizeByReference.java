package io.sisu.nng.jna;

import com.sun.jna.Native;

public class SizeByReference extends NumberByReference {
    public Size getSize() {
        Size size = new Size();

        if (Native.SIZE_T_SIZE == 8) {
            size.setValue(getPointer().getLong(0));
        } else {
            // XXX: YOLO
            size.setValue(getPointer().getInt(0));
        }
        size.converted = true;
        return size;
    }
}
