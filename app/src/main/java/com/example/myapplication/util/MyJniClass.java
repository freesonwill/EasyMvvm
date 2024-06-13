package com.example.myapplication.util;

public class MyJniClass {

    // Native方法声明
    public native String jniMethod();

    // 加载JNI库
    static {
//        System.loadLibrary("hellocpp");
        System.loadLibrary("main");
    }
}
