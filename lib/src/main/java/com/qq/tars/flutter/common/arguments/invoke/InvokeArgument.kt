package com.qq.tars.flutter.common.arguments.invoke

import com.qq.tars.codec.TarsOutputStream
import com.qq.tars.codec.TarsStruct
import com.qq.tars.flutter.common.TarsMessageCodec
import com.qq.tars.flutter.common.TarsMethodCodec


interface InvokeArgument {
    fun getArguments(): List<Any>

    fun encode(stream: TarsMessageCodec.ExposedByteArrayOutputStream): ByteArray {
        val arguments = getArguments()
//        val stream = TarsMessageCodec.ExposedByteArrayOutputStream()
        stream.write(TarsMessageCodec.LIST.toInt())
        TarsMessageCodec.writeSize(stream, arguments.size)
        arguments.forEach {
            write(it, stream)
        }
        return stream.toByteArray()
    }

    fun write(it: Any, stream: TarsMessageCodec.ExposedByteArrayOutputStream) {
        val messageCodec = TarsMethodCodec.INSTANCE.messageCodec
        when (it) {
            is List<*> -> {
                stream.write(TarsMessageCodec.LIST.toInt())
                TarsMessageCodec.writeSize(stream, it.size)
                for (i in 0 until it.size) {
                    write(it[i]!!, stream)
                }
            }
            is Map<*, *> -> {
                stream.write(TarsMessageCodec.MAP.toInt())
                TarsMessageCodec.writeSize(stream, it.size)
                val entries = it.entries
                entries.forEach {
                    write(it.key!!, stream)
                    write(it.value!!, stream)
                }
            }
            is TarsStruct -> {
                val outputStream = TarsOutputStream()
                outputStream.setServerEncoding("UTF-8")
                it.writeTo(outputStream)
                val bytes = outputStream.toByteArray()
                stream.write(TarsMessageCodec.BYTE_ARRAY.toInt())
                TarsMessageCodec.writeBytes(stream, bytes)
            }
            else -> {
                messageCodec.writeValue(stream, it)
            }
        }
    }

}