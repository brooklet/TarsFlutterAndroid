package com.qq.tars.flutter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.qq.tars.flutter.common.TarsMethodChannel
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument0
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor


/**
 * This binds a FlutterEngine instance with the DataModel and a channel for communicating with that
 * engine.
 *
 * Messages involving the DataModel are handled by the EngineBindings, other messages are forwarded
 * to the EngineBindingsDelegate.
 *
 * @see main.dart for what messages are getting sent from Flutter.
 */
class EngineBindings(
    engines: FlutterEngineGroup,
    context: Context,
    flutterMethodChannel: FlutterMethodChannel,
    entrypoint: String
) {
    val engine: FlutterEngine
    private val mFlutterMethodChannel = flutterMethodChannel
    val channel: TarsMethodChannel

    init {
        // This has to be lazy to avoid creation before the FlutterEngineGroup.
        val dartEntrypoint =
            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(), entrypoint
            )
        engine = engines.createAndRunEngine(context, dartEntrypoint)
        channel =
            TarsMethodChannel(
                engine.dartExecutor.binaryMessenger,
                mFlutterMethodChannel.getChannelName()
            )
    }

    /**
     * This setups the messaging connections on the platform channel and the DataModel.
     */
    fun attach() {
        mFlutterMethodChannel.attach(channel)
        channel.registerMethodCallHandler(mFlutterMethodChannel.getMethodCallHandlers())
    }


    /**
     * This tears down the messaging connections on the platform channel and the DataModel.
     */
    fun detach() {
        engine.destroy()
        mFlutterMethodChannel.detach(channel)
        channel.clearMethodCallHandler()
    }
}
