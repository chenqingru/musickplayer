package com.seed.network;

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;
import com.seed.env.GlobalConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeedNetEngine {
    private static final SeedNetEngine ourInstance = new SeedNetEngine();
    private static final long CONNECT_TIME_OUT = 30 * 1000L;
    private static final long READ_TIME_OUT = 30 * 1000L;
    private static final long WRITE_TIME_OUT = 30 * 1000L;

    public static SeedNetEngine ins() {
        return ourInstance;
    }

    private Retrofit mRetrofit;

    private SeedNetEngine() {
    }


    public <T> T get(Class<T> service) {
        return mRetrofit.create(service);
    }


    public void initRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        mRetrofit = builder.client(createOkHttpClient())
                .baseUrl(GlobalConstants.Net.GLOBAL_NET_HOST)
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
                //失败重试
                .retryOnConnectionFailure(true)
                // 添加公共Header的拦截器
                .addInterceptor(new CommonHeaderInterceptor()).build();
    }
}
