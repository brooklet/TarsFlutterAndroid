package com.qq.tars.flutter

import com.qq.tars.flutter.common.TarsMethodChannel

interface FlutterMethodChannel {
    fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler>
    fun getChannelName(): String
    fun attach(channel:TarsMethodChannel) {}
    fun detach(channel:TarsMethodChannel) {}
}