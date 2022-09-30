package com.seed.network;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CommonHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 添加公共Header
        Headers originalHeaders = originalRequest.headers();
        Headers.Builder headersBuilder = originalHeaders.newBuilder();
        headersBuilder.set("commonHeader1","1");
        headersBuilder.set("version","12.0.0");
        Request newRequest = originalRequest.newBuilder()
                .headers(headersBuilder.build())
                .build();
        return chain.proceed(newRequest);

    }
}
