package com.qq.tars.flutter.common;

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument;
import com.qq.tars.flutter.common.arguments.register.RegisterParameter;

import java.nio.ByteBuffer;

import io.flutter.plugin.common.FlutterException;
import io.flutter.plugin.common.MethodCall;

/**
 * A codec for method calls and enveloped results.
 *
 * <p>Method calls are encoded as binary messages with enough structure that the codec can extract a
 * method name String and an arguments Object. These data items are used to populate a {@link
 * MethodCall}.
 *
 * <p>All operations throw {@link IllegalArgumentException}, if conversion fails.
 */
public interface MethodCodec {
    /**
     * Encodes a message call into binary.
     *
     * @param methodCall a {@link TarsMethodCall}.
     * @return a {@link ByteBuffer} containing the encoding between position 0 and the current
     *     position.
     */
    ByteBuffer encodeMethodCall(TarsMethodCall methodCall);

//    /**
//     * Decodes a message call from binary.
//     *
//     * @param methodCall the binary encoding of the method call as a {@link ByteBuffer}.
//     * @return a {@link MethodCall} representation of the bytes between the given buffer's current
//     *     position and its limit.
//     */
//    TarsMethodCall decodeMethodCall(ByteBuffer methodCall);

    String decodeMethod(ByteBuffer methodCall);

    InvokeArgument decodeMethodArguments(ByteBuffer methodCall, RegisterParameter resultParameter);

    /**
     * Encodes a successful result into a binary envelope message.
     *
     * @param result The result value, possibly null.
     * @return a {@link ByteBuffer} containing the encoding between position 0 and the current
     *     position.
     */
    ByteBuffer encodeSuccessEnvelope(InvokeArgument result);

    /**
     * Encodes an error result into a binary envelope message.
     *
     * @param errorCode An error code String.
     * @param errorMessage An error message String, possibly null.
     * @param errorDetails Error details, possibly null. Consider supporting {@link Throwable} in your
     *     codec. This is the most common value passed to this field.
     * @return a {@link ByteBuffer} containing the encoding between position 0 and the current
     *     position.
     */
    ByteBuffer encodeErrorEnvelope(String errorCode, String errorMessage, Object errorDetails);

    /**
     * Encodes an error result into a binary envelope message with the native stacktrace.
     *
     * @param errorCode An error code String.
     * @param errorMessage An error message String, possibly null.
     * @param errorDetails Error details, possibly null. Consider supporting {@link Throwable} in your
     *     codec. This is the most common value passed to this field.
     * @param errorStacktrace Platform stacktrace for the error. possibly null.
     * @return a {@link ByteBuffer} containing the encoding between position 0 and the current
     *     position.
     */
    ByteBuffer encodeErrorEnvelopeWithStacktrace(
            String errorCode, String errorMessage, Object errorDetails, String errorStacktrace);

    /**
     * Decodes a result envelope from binary.
     *
     * @param envelope the binary encoding of a result envelope as a {@link ByteBuffer}.
     * @return the enveloped result Object.
     * @throws FlutterException if the envelope was an error envelope.
     */
    InvokeArgument decodeEnvelope(ByteBuffer envelope, RegisterParameter resultParameter);
}
