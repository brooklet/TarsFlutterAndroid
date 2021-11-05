package com.qq.tars.flutter.common.arguments.register

import com.qq.tars.codec.TarsInputStream
import com.qq.tars.codec.TarsStruct
import com.qq.tars.flutter.common.TarsMessageCodec
import com.qq.tars.flutter.common.TarsMethodCodec
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument
import java.nio.ByteBuffer
import java.util.HashMap


interface RegisterParameter {
    fun getMappingParameter(): List<Any>
    fun dispatchResult(result: List<Any>): InvokeArgument

    fun decode(buffer: ByteBuffer): InvokeArgument {
        val mappingParameter = getMappingParameter()
        val result = ArrayList<Any>()
        val type = buffer.get()
        val size = TarsMessageCodec.readSize(buffer)
        mappingParameter.forEach {
            val obj = decode(it, buffer)
            result.add(obj)
        }

        return dispatchResult(result)
    }

    fun decode(it: Any, buffer: ByteBuffer): Any {
        val messageCodec = TarsMethodCodec.INSTANCE.messageCodec
        val type = buffer.get()

        val classType = if (it is Class<*>) {
            it
        } else {
            (it::class.java)
        }

        when {
            List::class.java.isAssignableFrom(classType) -> {
                val size = TarsMessageCodec.readSize(buffer)
                val obj = ArrayList<Any>(size)
                for (i in 0 until size) {
                    obj.add(decode((it as List<*>)[0]!!, buffer))
                }
                return obj
            }
            Map::class.java.isAssignableFrom(classType) -> {
                val size = TarsMessageCodec.readSize(buffer)
                val obj = HashMap<Any, Any>()
                for (i in 0 until size) {
                    obj[decode((it as Map<*, *>).entries.first().key!!, buffer)] =
                        decode((it as Map<*, *>).entries.first().value!!, buffer)
                }
                return obj
            }
            TarsStruct::class.java.isAssignableFrom(classType) -> {
                val obj = if (it is Class<*>) {
                    it.newInstance() as TarsStruct
                } else {
                    (it::class.java).newInstance() as TarsStruct
                }

                val bytes =
                    messageCodec.readValueOfType(TarsMessageCodec.BYTE_ARRAY, buffer) as ByteArray
                val inputStream = TarsInputStream(bytes)
                inputStream.setServerEncoding("UTF-8")
                obj.readFrom(inputStream)
                return obj
            }
            else -> {
                return messageCodec.readValueOfType(type, buffer)
            }
        }
    }

}