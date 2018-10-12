package com.demo.photo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.demo.photo.util.LogUtil;

import java.io.InputStream;

import lib.kalu.glide.LibInterceptor;
import lib.kalu.glide.LibModelLoader;
import okhttp3.OkHttpClient;

@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        LogUtil.e("kalu", "applyOptions");

    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        LogUtil.e("kalu", "registerComponents");

        // step1
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new LibInterceptor());
        // step2
        final OkHttpClient okHttpClient = builder.build();
        // step3
        registry.replace(GlideUrl.class, InputStream.class, new LibModelLoader.Factory(okHttpClient));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        LogUtil.e("kalu", "isManifestParsingEnabled");
        return false;
    }
}