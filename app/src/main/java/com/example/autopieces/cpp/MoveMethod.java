package com.example.autopieces.cpp;

public class MoveMethod {

    static {
        System.loadLibrary("MoveMethod");
    }

    public static native int[] calculateMovePath(
            int startX, int startY,
            int endX, int endY,
            int[] map,int row,int col);
}
