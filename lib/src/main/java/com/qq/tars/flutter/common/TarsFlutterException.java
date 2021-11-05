package com.qq.tars.flutter.common;


import io.flutter.BuildConfig;
import io.flutter.Log;

public class TarsFlutterException  extends RuntimeException {
    private static final String TAG = "FlutterException#";

    public final String code;
    public final Object details;

    TarsFlutterException(String code, String message, Object details) {
        super(message);
        if (BuildConfig.DEBUG && code == null) {
            Log.e(TAG, "Parameter code must not be null.");
        }
        this.code = code;
        this.details = details;
    }
}