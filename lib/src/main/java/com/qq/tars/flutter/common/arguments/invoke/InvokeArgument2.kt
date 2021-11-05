package com.qq.tars.flutter.common.arguments.invoke


class InvokeArgument2(val arg1: Any, val arg2: Any) : InvokeArgument {

    override fun getArguments(): List<Any> {
        return listOf(arg1, arg2)
    }

}