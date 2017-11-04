package com.demo.photo.glide.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.demo.photo.glide.listener.OnGlideLoadChangeListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * description: Glide外部缓存路径
 * created by kalu on 17-10-15 上午5:00
 */
public class GlideOkHttpClientManager extends OkHttpClient {

    private OkHttpClient build;
    private OnGlideLoadChangeListener listener;

    private GlideOkHttpClientManager() {
        Log.e("kalu", "GlideOkHttpClient");

        build = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        return response.newBuilder()
                                .body(new GlideOkhttpResponseBody(request.url().toString(), response.body(), listener))
                                .build();
                    }
                })
                .build();
    }

    OkHttpClient getOkHttpClient() {
        return build;
    }

    public void setOnGlideLoadChangeListener(OnGlideLoadChangeListener listener) {
        this.listener = listener;
    }

    /**
     * ************************************ 单例 **********************************************
     */

    /**
     * 这种方式不仅能够保证线程安全，也能保证单例对象的唯一性，同时也延迟实例化，是一种非常推荐的方式。
     */
    public static GlideOkHttpClientManager getInstance() {
        return GlideOkHttpClientManager.SingletonHolder.instance;
    }

    /**
     * 静态内部类与外部类的实例没有绑定关系，而且只有被调用时才会
     * 加载，从而实现了延迟加载
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static GlideOkHttpClientManager instance = new GlideOkHttpClientManager();
    }
}
