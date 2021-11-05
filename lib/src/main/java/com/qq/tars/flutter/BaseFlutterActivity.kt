package com.qq.tars.flutter

import android.content.Context
import android.os.Bundle
import com.qq.tars.flutter.common.TarsMethodChannel
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineGroup
import java.util.*

abstract class BaseFlutterActivity : FlutterActivity() {

    abstract fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler>
    abstract fun getChannelName(): String
    abstract fun getEntrypoint(): String
    abstract fun getFlutterEngineGroup(): FlutterEngineGroup

    open fun attach(channel: TarsMethodChannel) {
    }

    open fun detach(channel: TarsMethodChannel) {
    }

    inner class MyFlutterMethodChannel : FlutterMethodChannel {
        override fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler> {
            val methodList = mutableListOf<TarsMethodChannel.MethodHandler>()
            methodList.addAll(this@BaseFlutterActivity.getMethodCallHandlers())
            return methodList
        }

        override fun getChannelName(): String {
            return this@BaseFlutterActivity.getChannelName()
        }

        override fun attach(channel: TarsMethodChannel) {
            super.attach(channel)
            this@BaseFlutterActivity.attach(channel);
        }

        override fun detach(channel: TarsMethodChannel) {
            super.detach(channel)
            this@BaseFlutterActivity.detach(channel);
        }
    }

    open val engineBindings: EngineBindings by lazy {
        EngineBindings(
            getFlutterEngineGroup(),
            context = this,
            flutterMethodChannel = MyFlutterMethodChannel(),
            entrypoint = getEntrypoint()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        engineBindings.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        engineBindings.detach()
    }

    override fun provideFlutterEngine(context: Context): FlutterEngine? {
        return engineBindings.engine
    }

    fun getMethodChannel(): TarsMethodChannel {
        return engineBindings.channel
    }

}