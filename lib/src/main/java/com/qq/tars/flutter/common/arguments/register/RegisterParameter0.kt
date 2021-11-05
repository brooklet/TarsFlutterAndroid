package com.qq.tars.flutter.common.arguments.register

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument0

class RegisterParameter0() : RegisterParameter {

    override fun getMappingParameter(): List<Any> {
        return listOf()
    }

    override fun dispatchResult(result: List<Any>): InvokeArgument {
        return InvokeArgument0()
    }

}