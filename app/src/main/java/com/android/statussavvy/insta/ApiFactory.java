package com.android.statussavvy.insta;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class ApiFactory {
    ApiFactory() {
    }

    static InstaService getInstaService(Interceptor interceptor) {
        return (InstaService) getRetrofit(buildClient(interceptor)).create(InstaService.class);
    }

    private static Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(InstaService.ENDPOINT).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create())).addCallAdapterFactory(RxJava3CallAdapterFactory.create()).client(okHttpClient).build();
    }

    private static OkHttpClient buildClient(Interceptor interceptor) {
        return new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).addInterceptor(interceptor).build();
    }
}
