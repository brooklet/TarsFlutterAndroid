package com.qq.tars.flutter.common;

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument;
import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument0;
import com.qq.tars.flutter.common.arguments.register.RegisterParameter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 */
public class TarsMethodCodec implements MethodCodec {
    public static final TarsMethodCodec INSTANCE =
            new TarsMethodCodec(TarsMessageCodec.INSTANCE);
    private final TarsMessageCodec messageCodec;

    /**
     * Creates a new method codec based on the specified message codec.
     */
    public TarsMethodCodec(TarsMessageCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    public TarsMessageCodec getMessageCodec() {
        return messageCodec;
    }

    @Override
    public ByteBuffer encodeMethodCall(TarsMethodCall methodCall) {
        final TarsMessageCodec.ExposedByteArrayOutputStream stream = new TarsMessageCodec.ExposedByteArrayOutputStream();
        messageCodec.writeValue(stream, methodCall.method);
        InvokeArgument arguments = methodCall.arguments;
        arguments.encode(stream);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override
    public String decodeMethod(ByteBuffer methodCall) {
        methodCall.order(ByteOrder.nativeOrder());
        final Object method = messageCodec.readValue(methodCall);
        if (!(method instanceof String)) {
            throw new IllegalArgumentException("Method call corrupted");
        }
        return (String) method;
    }

    @Override
    public InvokeArgument decodeMethodArguments(ByteBuffer methodCall, RegisterParameter resultParameter) {
        if (resultParameter == null) {
            return new InvokeArgument0();
        }
        return resultParameter.decode(methodCall);
    }


    @Override
    public ByteBuffer encodeSuccessEnvelope(InvokeArgument result) {
        final TarsMessageCodec.ExposedByteArrayOutputStream stream = new TarsMessageCodec.ExposedByteArrayOutputStream();
        stream.write(0);
        result.encode(stream);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override
    public ByteBuffer encodeErrorEnvelope(
            String errorCode, String errorMessage, Object errorDetails) {
        final TarsMessageCodec.ExposedByteArrayOutputStream stream = new TarsMessageCodec.ExposedByteArrayOutputStream();
        stream.write(1);
        messageCodec.writeValue(stream, errorCode);
        messageCodec.writeValue(stream, errorMessage);
        if (errorDetails instanceof Throwable) {
            messageCodec.writeValue(stream, getStackTrace((Throwable) errorDetails));
        } else {
            messageCodec.writeValue(stream, errorDetails);
        }
        final ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override
    public ByteBuffer encodeErrorEnvelopeWithStacktrace(
            String errorCode, String errorMessage, Object errorDetails, String errorStacktrace) {
        final TarsMessageCodec.ExposedByteArrayOutputStream stream = new TarsMessageCodec.ExposedByteArrayOutputStream();
        stream.write(1);
        messageCodec.writeValue(stream, errorCode);
        messageCodec.writeValue(stream, errorMessage);
        if (errorDetails instanceof Throwable) {
            messageCodec.writeValue(stream, getStackTrace((Throwable) errorDetails));
        } else {
            messageCodec.writeValue(stream, errorDetails);
        }
        messageCodec.writeValue(stream, errorStacktrace);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override
    public InvokeArgument decodeEnvelope(ByteBuffer envelope, RegisterParameter resultParameter) {
        if (resultParameter == null) {
            return new InvokeArgument0();
        }
        envelope.order(ByteOrder.nativeOrder());
        final byte flag = envelope.get();
        switch (flag) {
            case 0: {
                byte type = envelope.get();
                byte[] bytes = (byte[]) messageCodec.readValueOfType(type, envelope);
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                return resultParameter.decode(buffer);
            }
            // Falls through intentionally.
            case 1: {
                final Object code = messageCodec.readValue(envelope);
                final Object message = messageCodec.readValue(envelope);
                final Object details = messageCodec.readValue(envelope);
                if (code instanceof String
                        && (message == null || message instanceof String)
                        && !envelope.hasRemaining()) {
                    throw new TarsFlutterException((String) code, (String) message, details);
                }
            }
        }
        throw new IllegalArgumentException("Envelope corrupted");
    }

    private static String getStackTrace(Throwable t) {
        Writer result = new StringWriter();
        t.printStackTrace(new PrintWriter(result));
        return result.toString();
    }
}
