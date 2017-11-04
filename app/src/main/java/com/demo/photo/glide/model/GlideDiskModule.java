package com.demo.photo.glide.model;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * description: Glide外部缓存路径
 * created by kalu on 2017-10-15
 */
@GlideModule
public class GlideDiskModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);

        long size = Runtime.getRuntime().maxMemory() / 8;
        builder.setDiskCache(new GlideDiskCache("image", (int) size))
                .setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);

        OkHttpClient okHttpClient = GlideOkHttpClientManager.getInstance().getOkHttpClient();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

    /**
     * 清单解析和旧GlideModule界面已被弃用，但在v4中仍然支持。
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
