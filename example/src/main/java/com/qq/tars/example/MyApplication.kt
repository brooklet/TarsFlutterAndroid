package com.qq.tars.example

import android.app.Application
import io.flutter.embedding.engine.FlutterEngineGroup

class MyApplication : Application() {
    lateinit var engines: FlutterEngineGroup

    companion object {
        private lateinit var sInstance: MyApplication
        fun getInstance(): MyApplication {
            return sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this

        //flutter 专用EngineBindings.kt
        engines = FlutterEngineGroup(this)
        //初始化一个默认引擎，后面打开页面显示更快
        engines.createAndRunDefaultEngine(this)
    }
}