package com.qq.tars.example

import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.qq.tars.example.idl.TestReq
import com.qq.tars.example.idl.TestRsp
import com.qq.tars.flutter.BaseFlutterActivity
import com.qq.tars.flutter.common.TarsMethodChannel
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument1
import com.qq.tars.flutter.common.arguments.register.RegisterParameter0
import com.qq.tars.flutter.common.arguments.register.RegisterParameter1
import io.flutter.embedding.engine.FlutterEngineGroup
import kotlin.random.Random

class MainActivity : BaseFlutterActivity() {

    var count = Random.Default.nextInt(10000)

    override fun attach(channel: TarsMethodChannel) {
        val req = TestReq()
        req.id = count
        Log.d("MainActivity", "count:${req.id}")
        channel.invokeMethod("onCountChange", InvokeArgument1(req))
    }

    // 注册可供flutter调用的方法
    override fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler> {
        return mutableListOf(
            TarsMethodChannel.MethodHandler(
                "getCount", RegisterParameter0()
            ) { methodCall, result ->
                run {
                    val rsp = TestRsp()
                    rsp.id = count.toLong()
                    result.success(InvokeArgument1(rsp))
                }
            },

            TarsMethodChannel.MethodHandler(
                "updateCount", RegisterParameter1(TestReq::class.java)
            ) { methodCall, result ->
                run {
                    Log.d("MainActivity", "call method: updateCount.")
                    val req = (methodCall.arguments as InvokeArgument1).arg1 as TestReq
                    count += req.id
                    val rsp = TestRsp()
                    rsp.id = count.toLong()
                    result.success(InvokeArgument1(rsp))
                }
            }
        )
    }

    // 定义TarsMethodChannel的名称,需要与flutter中一致
    override fun getChannelName(): String {
        return "test"
    }

    // 定义flutter中的页面的入口名称, 需要与flutter中一致,
    // 如'testPage' 对应 flutter @pragma('vm:testPage')
    override fun getEntrypoint(): String {
        return "testPage"
    }

    // 获得 FlutterEngineGroup
    override fun getFlutterEngineGroup(): FlutterEngineGroup {
        return MyApplication.getInstance().engines
    }

}