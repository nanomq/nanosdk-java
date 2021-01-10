package io.sisu.nng.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class SizeByReference extends ByReference {
    public SizeByReference() {
        this(null);
    }

    public SizeByReference(Pointer value) {
        super(Native.POINTER_SIZE);
        setValue(value);
    }

    public void setValue(Pointer value) {
        getPointer().setPointer(0, value);
    }

    public Pointer getValue() {
        return getPointer().getPointer(0);
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
