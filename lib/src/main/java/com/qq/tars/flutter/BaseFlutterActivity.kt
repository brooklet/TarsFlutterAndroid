package com.qq.tars.flutter

import android.content.Context
import android.os.Bundle
import com.qq.tars.flutter.common.TarsMethodChannel
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineGroup
import java.util.*

abstract class BaseFlutterActivity : FlutterActivity() {

    // 注册可供flutter调用的TarsMethodChannel方法列表
    abstract fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler>

    // 定义TarsMethodChannel的名称,需要与flutter中一致
    abstract fun getChannelName(): String

    // 定义flutter中的页面的入口名称, 需要与flutter中一致,
    // 如'testPage' 对应 flutter @pragma('vm:testPage')
    abstract fun getEntrypoint(): String

    // 获得 FlutterEngineGroup
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