package com.qq.tars.flutter.common.arguments.invoke


class InvokeArgument3(val arg1: Any, val arg2: Any, val arg3: Any) : InvokeArgument {

    override fun getArguments(): List<Any> {
        return listOf(arg1, arg2, arg3)
    }

}