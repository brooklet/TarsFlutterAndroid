package com.qq.tars.flutter.common.arguments.invoke

class InvokeArgument1(val arg1: Any) : InvokeArgument {

    override fun getArguments(): List<Any> {
        return listOf(arg1)
    }


}