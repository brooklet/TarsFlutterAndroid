package com.qq.tars.flutter.common.arguments.register

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument2
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument3


class RegisterParameter3(val arg1: Any, val arg2: Any, val arg3: Any) : RegisterParameter {

    override fun getMappingParameter(): List<Any> {
        return listOf(arg1, arg2, arg3)
    }

    override fun dispatchResult(result: List<Any>): InvokeArgument {
        return InvokeArgument3(result[0], result[1], result[2])
    }

}