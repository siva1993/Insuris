package com.siva.insuris.service;

import android.content.Context;

import com.siva.insuris.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by developer on 6/11/17.
 */

public class NoConnectivityInterceptor implements Interceptor {
    private Context context;

    public NoConnectivityInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!Utils.isNetworkAvailable(context)) {
            throw new NoConnectivityException();
        }

        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Content-Type", "application/json");
        Request request = builder.build();
        return chain.proceed(request);

    }

    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "Connect Internet";
        }
    }
}
