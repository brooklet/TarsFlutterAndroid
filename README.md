# TarsFlutterAndroid

Flutter Add-to-app Android Support Library For Tars RPC framework

在Flutter模块集成到已有Android应用模式下，实现在Flutter模块和Android原生层之间传递Tars对象

> 如果你是直接在Flutter模块中使用Tars协议或者TUP协议请求后端数据接口，**不需要使用本项目**，直接使用这个项目[https://pub.dev/packages/tars_flutter](https://pub.dev/packages/tars_flutter)

---

# 引入方法

1. 在Android的app模块`build.gradle`中添加：

```
dependencies {
    implementation 'com.github.brooklet:TarsFlutterAndroid:v1.0.0-beta.3'
}
```

2. 在Flutter模块`pubspec.yaml`中添加：

```
dependencies:
  tars_flutter: ^0.0.8

```

3. Tars定义文件转dart文件的方法，参考 [https://pub.dev/packages/tars_flutter](https://pub.dev/packages/tars_flutter)



# 快速入门

1. Android 的 MyApplication 中初始化 FlutterEngineGroup

    ```

    class MyApplication : Application() {
        lateinit var engines: FlutterEngineGroup
        ...

        override fun onCreate() {
            ...

            engines = FlutterEngineGroup(this)
            //初始化一个默认引擎，后面打开页面显示更快
            engines.createAndRunDefaultEngine(this)
        }
    }

    ```


2. 页面直接继承封装好的：`BaseFlutterActivity` 或者 `BaseFlutterFragment`，需实现如下方法：

    ```
        // 注册可供flutter调用的TarsMethodChannel方法列表
        abstract fun getMethodCallHandlers(): List<TarsMethodChannel.MethodHandler>

        // 定义TarsMethodChannel的名称,需要与flutter中一致
        abstract fun getChannelName(): String

        // 定义flutter中的页面的入口名称, 需要与flutter中一致,
        // 如'testPage' 对应 flutter @pragma('vm:testPage')
        abstract fun getEntrypoint(): String

        // 获得 FlutterEngineGroup
        abstract fun getFlutterEngineGroup(): FlutterEngineGroup

    ```

        实例如下：

    ```
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


    ```

    3. 在 Flutter模块中创建 TarsMethodChannel 对象，并注册Android原生层可以调用的方法列表

    ```
        late TarsMethodChannel _channel;

        @override
        void initState() {
            super.initState();
            _channel = TarsMethodChannel('test');
            registerMethodCallHandlers();
            ...
        }

        //注册回调函数
        void registerMethodCallHandlers() {
            _channel.registerMethodCallHandlers([
            //onCountChange begin
            MethodHandler("onCountChange", RegisterParameter1(TestReq()),
                (TarsMethodCall call) async {
                logger.d("message");
                var req = (call.arguments as InvokeArgument1).arg1 as TestReq;
                _counter = req.id;
                logger.d("get onCountChange call: $_counter");

                setState(() {});
            }),
            //onCountChange end
            //add more...
            ]);
        }

    ```

    4. 在Android中调用Flutter方法的方式

    ```
        val req = TestReq()
        req.id = count
        channel.invokeMethod("onCountChange", InvokeArgument1(req))
    ```

    > 其中`TestReq`是Tars协议的结构体

    5. 在Flutter中调用Android方法的方式

    ```
        Future<void> _incrementCounter() async {
            var req = TestReq();
            req.id = 1;
            var response = await _channel.invokeMethod<InvokeArgument1>("updateCount",
                InvokeArgument1(req), false, RegisterParameter1(TestRsp()));
            var rsp = response!.arg1 as TestRsp;
            _counter = rsp.id;
            logger.d("updateCount: $_counter");
            setState(() {});
        }
    ```

    > 其中`TestReq` 和 `TestRsp` 都是Tars协议的结构体

---

# 用法说明

1. TarsMethodChannel是在Flutter自带的MethodChannel基础增加了对Tars对象的支持。

2. 相比MethodChannel的调用方式，TarsMethodChannel增加了`InvokeArgument` 和 `RegisterParameter`参数。

3. `InvokeArgument`是调用方，用来封装多个调用参数，方便传输时封包，包括`InvokeArgument0`，`InvokeArgument1`，`InvokeArgument2`，`InvokeArgument3`四个子类，分别对应传输0..3个参数的情况。

4. `RegisterParameter`是被调用方，用来注册多个被调用参数，方便传输时解包，包括`RegisterParameter0`，`RegisterParameter1`，`RegisterParameter2`，`RegisterParameter3`四个子类，分别对应传输0..3个参数的情况。

5. 在Flutter中，`InvokeArgument` 及 `RegisterParameter` 的参数都必须是 **对象，而不是类**，在Android中`RegisterParameter` 的参数可以是类。

6. 调用方的`InvokeArgument`必须和被调用方的`RegisterParameter`的类型匹配，比如`InvokeArgument2(TestReq(),"")`必须匹配被调用方注册的`RegisterParameter2(TestReq(),"")`

---

# 备注

> 此框架仅支持Flutter 2.2.0及以上版本

> 详细用法参考 example 
