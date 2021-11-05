package com.qq.tars.flutter.common;


import androidx.annotation.Nullable;

import com.qq.tars.flutter.common.arguments.invoke.InvokeArgument;

import org.json.JSONObject;

import java.util.Map;

import io.flutter.BuildConfig;
import io.flutter.plugin.common.MethodCall;

public class TarsMethodCall {
    /**
     * The name of the called method.
     */
    public final String method;

    /**
     * Arguments for the call.
     *
     */
    public final InvokeArgument arguments;

    /**
     * Creates a {@link TarsMethodCall} with the specified method name and arguments.
     *
     * @param method    the method name String, not null.
     * @param arguments the arguments, a value supported by the channel's message codec.
     */
    public TarsMethodCall(String method, InvokeArgument arguments) {
        if (BuildConfig.DEBUG && method == null) {
            throw new AssertionError("Parameter method must not be null.");
        }
        this.method = method;
        this.arguments = arguments;
    }

    /**
     * Returns the arguments of this method call with a static type determined by the call-site.
     *
     * @param <T> the intended type of the arguments.
     * @return the arguments with static type T
     */
    @SuppressWarnings("unchecked")
    public <T> T arguments() {
        return (T) arguments;
    }

}
