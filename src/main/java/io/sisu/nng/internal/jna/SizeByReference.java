package io.sisu.nng.internal.jna;

import com.sun.jna.Native;

public class SizeByReference extends NumberByReference {
    public void setSize(Size size) {
        if (Native.SIZE_T_SIZE == 8) {
            getPointer().setLong(0, size.convert());
        } else {
            // XXX: YOLO
            getPointer().setInt(0, (int) size.convert());
        }
        size.converted = true;
    }

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
