package com.qq.tars.flutter.common.arguments.register

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument0
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument1


class RegisterParameter1(val arg1: Any) : RegisterParameter {

    override fun getMappingParameter(): List<Any> {
        return listOf(arg1)
    }

    override fun dispatchResult(result: List<Any>): InvokeArgument {
        return InvokeArgument1(result[0])
    }

}