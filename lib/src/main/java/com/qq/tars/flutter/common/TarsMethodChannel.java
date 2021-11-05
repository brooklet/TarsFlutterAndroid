package com.qq.tars.flutter.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument;
import com.qq.tars.flutter.common.arguments.register.RegisterParameter;
import com.qq.tars.flutter.common.arguments.register.RegisterParameter0;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.BuildConfig;
import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.FlutterException;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodCodec;
import io.flutter.plugin.common.StandardMessageCodec;

public class TarsMethodChannel {
    private static final String TAG = "TarsMethodChannel#";

    private final BinaryMessenger messenger;
    private final String name;
    private final TarsMethodCodec codec;
    private final IncomingMethodCallHandler incomingMethodCallHandler;

    /**
     * Creates a new channel associated with the specified {@link BinaryMessenger} and with the
     * specified name and the standard {@link MethodCodec}.
     *
     * @param messenger a {@link BinaryMessenger}.
     * @param name      a channel name String.
     */
    public TarsMethodChannel(BinaryMessenger messenger, String name) {
        this(messenger, name, TarsMethodCodec.INSTANCE);
    }

    /**
     * Creates a new channel associated with the specified {@link BinaryMessenger} and with the
     * specified name and {@link MethodCodec}.
     *
     * @param messenger a {@link BinaryMessenger}.
     * @param name      a channel name String.
     * @param codec     a {@link MessageCodec}.
     */
    public TarsMethodChannel(BinaryMessenger messenger, String name, TarsMethodCodec codec) {
        if (BuildConfig.DEBUG) {
            if (messenger == null) {
                Log.e(TAG, "Parameter messenger must not be null.");
            }
            if (name == null) {
                Log.e(TAG, "Parameter name must not be null.");
            }
            if (codec == null) {
                Log.e(TAG, "Parameter codec must not be null.");
            }
        }
        this.messenger = messenger;
        this.name = name;
        this.codec = codec;
        this.incomingMethodCallHandler = new TarsMethodChannel.IncomingMethodCallHandler();
        this.messenger.setMessageHandler(name, incomingMethodCallHandler);
    }

    /**
     * Invokes a method on this channel, expecting no result.
     *
     * @param method    the name String of the method.
     * @param arguments the arguments for the invocation, possibly null.
     */
    @UiThread
    public void invokeMethod(@NonNull String method, @Nullable InvokeArgument arguments) {
        invokeMethod(method, arguments, null, null);
    }

    /**
     * Invokes a method on this channel, optionally expecting a result.
     *
     * <p>Any uncaught exception thrown by the result callback will be caught and logged.
     *
     * @param method    the name String of the method.
     * @param arguments the arguments for the invocation, possibly null.
     * @param callback  a {@link TarsMethodChannel.Result} callback for the invocation result, or null.
     */
    @UiThread
    public void invokeMethod(String method, @Nullable InvokeArgument arguments, @Nullable RegisterParameter resultParameter, @Nullable TarsMethodChannel.Result callback) {
        Log.d(TAG, "invokeMethod: " + method);
        messenger.send(
                name,
                codec.encodeMethodCall(new TarsMethodCall(method, arguments)),
                callback == null ? null : new TarsMethodChannel.IncomingResultHandler(resultParameter, callback));
    }

    @UiThread
    public void registerMethodCallHandler(final TarsMethodChannel.MethodHandler handler) {
        incomingMethodCallHandler.register(handler);
    }

    @UiThread
    public void registerMethodCallHandler(final List<TarsMethodChannel.MethodHandler> handlers) {
        incomingMethodCallHandler.register(handlers);
    }

    @UiThread
    public void clearMethodCallHandler() {
        incomingMethodCallHandler.clear();
    }

    /**
     * Adjusts the number of messages that will get buffered when sending messages to channels that
     * aren't fully set up yet. For example, the engine isn't running yet or the channel's message
     * handler isn't set up on the Dart side yet.
     */
    public void resizeChannelBuffer(int newSize) {
        // BasicMessageChannel.resizeChannelBuffer(messenger, name, newSize);
    }


    public static class MethodHandler {
        public String method;
        public RegisterParameter argumentsMapping;
        public MethodCallHandler methodCallHandler;

        public MethodHandler(String method, RegisterParameter argumentsMapping, MethodCallHandler methodCallHandler) {
            this.method = method;
            this.argumentsMapping = argumentsMapping;
            this.methodCallHandler = methodCallHandler;
        }

        public MethodHandler(String method, MethodCallHandler methodCallHandler) {
            this.method = method;
            this.argumentsMapping = null;
            this.methodCallHandler = methodCallHandler;
        }
    }

    /**
     * A handler of incoming method calls.
     */
    public interface MethodCallHandler {

        /**
         * Handles the specified method call received from Flutter.
         *
         * <p>Handler implementations must submit a result for all incoming calls, by making a single
         * call on the given {@link MethodChannel.Result} callback. Failure to do so will result in lingering Flutter
         * result handlers. The result may be submitted asynchronously. Calls to unknown or
         * unimplemented methods should be handled using {@link MethodChannel.Result#notImplemented()}.
         *
         * <p>Any uncaught exception thrown by this method will be caught by the channel implementation
         * and logged, and an error result will be sent back to Flutter.
         *
         * <p>The handler is called on the platform thread (Android main thread). For more details see
         * <a href="https://github.com/flutter/engine/wiki/Threading-in-the-Flutter-Engine">Threading in
         * the Flutter Engine</a>.
         *
         * @param result A {@link TarsMethodChannel.Result} used for submitting the result of the call.
         */
        @UiThread
        void onMethodCall(TarsMethodCall methodCall, @NonNull TarsMethodChannel.Result result);
    }

    /**
     * Method call result callback. Supports dual use: Implementations of methods to be invoked by
     * Flutter act as clients of this interface for sending results back to Flutter. Invokers of
     * Flutter methods provide implementations of this interface for handling results received from
     * Flutter.
     *
     * <p>All methods of this class must be called on the platform thread (Android main thread). For
     * more details see <a
     * href="https://github.com/flutter/engine/wiki/Threading-in-the-Flutter-Engine">Threading in the
     * Flutter Engine</a>.
     */
    public interface Result {

        /**
         * Handles a successful result.
         *
         * @param result The result, possibly null. The result must be an Object type supported by the
         *               codec. For instance, if you are using {@link StandardMessageCodec} (default), please see
         *               its documentation on what types are supported.
         */
        @UiThread
        void success(@Nullable InvokeArgument result);

        /**
         * Handles an error result.
         *
         * @param errorCode    An error code String.
         * @param errorMessage A human-readable error message String, possibly null.
         * @param errorDetails Error details, possibly null. The details must be an Object type
         *                     supported by the codec. For instance, if you are using {@link StandardMessageCodec}
         *                     (default), please see its documentation on what types are supported.
         */
        @UiThread
        void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails);

        /**
         * Handles a call to an unimplemented method.
         */
        @UiThread
        void notImplemented();
    }

    private final class IncomingResultHandler implements BinaryMessenger.BinaryReply {
        private final TarsMethodChannel.Result callback;
        private final RegisterParameter resultParameter;

        IncomingResultHandler(RegisterParameter resultParameter, TarsMethodChannel.Result callback) {
            this.callback = callback;
            this.resultParameter = resultParameter;
        }

        @Override
        @UiThread
        public void reply(ByteBuffer reply) {
            try {
                if (reply == null) {
                    callback.notImplemented();
                } else {
                    try {
                        callback.success(codec.decodeEnvelope(reply, resultParameter));
                    } catch (FlutterException e) {
                        callback.error(e.code, e.getMessage(), e.details);
                    }
                }
            } catch (RuntimeException e) {
                Log.e(TAG + name, "Failed to handle method call result", e);
            }
        }
    }

    private final class IncomingMethodCallHandler implements BinaryMessenger.BinaryMessageHandler {
        private final Map<String, MethodHandler> handlers = new HashMap<>();

        IncomingMethodCallHandler() {
        }

        public void clear() {
            this.handlers.clear();
        }

        public void register(MethodHandler handler) {
            this.handlers.put(handler.method, handler);
        }

        public void register(List<MethodHandler> newHandlers) {
            for (int i = 0; i < newHandlers.size(); i++) {
                this.handlers.put(newHandlers.get(i).method, newHandlers.get(i));
            }
        }

        @Override
        @UiThread
        public void onMessage(ByteBuffer message, final BinaryMessenger.BinaryReply reply) {
            final String method = codec.decodeMethod(message);
            if (!handlers.containsKey(method)) {
                reply.reply(null);
                return;
            }
            MethodHandler handler = handlers.get(method);
            InvokeArgument arguments = codec.decodeMethodArguments(message, handler.argumentsMapping);
            final TarsMethodCall call = new TarsMethodCall(method, arguments);
            try {
                handler.methodCallHandler.onMethodCall(call,
                        new TarsMethodChannel.Result() {
                            @Override
                            public void success(InvokeArgument result) {
                                reply.reply(codec.encodeSuccessEnvelope(result));
                            }

                            @Override
                            public void error(String errorCode, String errorMessage, Object errorDetails) {
                                reply.reply(codec.encodeErrorEnvelope(errorCode, errorMessage, errorDetails));
                            }

                            @Override
                            public void notImplemented() {
                                reply.reply(null);
                            }
                        });
            } catch (RuntimeException e) {
                Log.e(TAG + name, "Failed to handle method call", e);
                reply.reply(
                        codec.encodeErrorEnvelopeWithStacktrace(
                                "error", e.getMessage(), null, getStackTrace(e)));
            }
        }

        private String getStackTrace(Exception e) {
            Writer result = new StringWriter();
            e.printStackTrace(new PrintWriter(result));
            return result.toString();
        }
    }
}
