package com.common.face.api;

public class FaceUtil {
    static {
//        System.load("/system/lib64/libfaceutil.so");
        System.loadLibrary("faceutil");
    }
    public static native int LedSet(String name, int on);
}
