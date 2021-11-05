package com.qq.tars.flutter.common.arguments.register

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument1
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument2


class RegisterParameter2(val arg1: Any, val arg2: Any) : RegisterParameter {

    override fun getMappingParameter(): List<Any> {
        return listOf(arg1, arg2)
    }

    override fun dispatchResult(result: List<Any>): InvokeArgument {
        return InvokeArgument2(result[0], result[1])
    }

}