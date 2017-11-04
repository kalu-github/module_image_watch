package com.demo.photo.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.demo.photo.APP;
import com.demo.photo.R;
import com.demo.photo.glide.listener.OnGlideLoadChangeListener;
import com.demo.photo.glide.model.GlideOkHttpClientManager;
import com.demo.photo.glide.transformation.BlurTransformation;
import com.demo.photo.glide.transformation.CircleTransformation;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

/**
 * description: 图片加载工具类
 * created by kalu on 2016/11/19 11:58
 */
public class GlideUtil {

    private static final String TAG = GlideUtil.class.getSimpleName();

    private static final int defaultImage = R.mipmap.ic_launcher;
    private static final int errorImage = R.mipmap.ic_launcher;

    private static final StringBuffer stringBuilder = new StringBuffer();

    // 条件设置
    // DiskCacheStrategy.NONE  不缓存文件
    // DiskCacheStrategy.RESOURCE  只缓存原图
    // DiskCacheStrategy.RESULT  只缓存最终加载的图（默认的缓存策略）
    // DiskCacheStrategy.ALL  同时缓存原图和结果图
    private static final RequestOptions options = new RequestOptions()
            .centerInside()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

    public static void loadImageSimple(Activity activity, ImageView imageView, String url) {
//        String style = "!app_boot1";
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("");
        loadImage(activity, null, imageView, url, stringBuilder, 0, 0, 0, false, false, null);
    }

    public static void loadImageSimple(Fragment fragment, ImageView imageView, String url, OnGlideLoadChangeListener listener) {
//        String style = "!app_boot1";
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("");
        loadImage(null, fragment, imageView, url, stringBuilder, 0, 0, 0, false, false, listener);
    }

    public static void loadImageOriginal(Activity activity, ImageView imageView, String url, OnGlideLoadChangeListener listener) {
//        String style = "!app_boot1";
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("");
        loadImage(activity, null, imageView, url, stringBuilder, 0, 0, 0, false, true, listener);
    }

    public static void loadImageOriginal(Fragment fragment, ImageView imageView, String url, OnGlideLoadChangeListener listener) {
//        String style = "!app_boot1";
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("");
        loadImage(null, fragment, imageView, url, stringBuilder, 0, 0, 0, false, true, listener);
    }

    /**
     * @param imageView
     * @param url
     * @param style
     * @param defaultImage 默认占位图
     * @param blurRadiu    1-99, 越大越模糊
     * @param isCircle     圆形图片处理
     * @param listener     图片下载进度监听
     */
    private static void loadImage(Activity activity, Fragment fragment, final ImageView imageView, String url, CharSequence style, int defaultImage, int errorImage, int blurRadiu, boolean isCircle, boolean isOriginal, final OnGlideLoadChangeListener listener) {

        if (null == activity && null == fragment) return;

        // 网络图片
        if (url != null && url.length() > 0) {

            RequestManager requestManager = null;

            if (null != activity) {
                requestManager = Glide.with(activity);
            }

            if (null != fragment) {
                requestManager = Glide.with(fragment);
            }

            if (null == requestManager) return;

            if (defaultImage != 0) {
                options.placeholder(defaultImage);
            }

            if (errorImage != 0) {
                options.error(errorImage);
            }

            // CDN图片格式大小
            if (url.startsWith("https://img.cdn.")) {
                url += style;
            }

            if (isCircle) {
                options.bitmapTransform(new CircleTransformation());
            }

            if (blurRadiu > 0 && blurRadiu < 100) {
                options.bitmapTransform(new BlurTransformation(blurRadiu));
            }

            // 图片下载进度监听
            if (null != listener) {
                GlideOkHttpClientManager.getInstance().setOnGlideLoadChangeListener(listener);
            }

            if (isOriginal) {

                requestManager.asDrawable().load(url).apply(options).into(new SimpleTarget<Drawable>() {

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        imageView.setImageDrawable(resource);
                    }
                });
            } else {

                requestManager.load(url).apply(options).into(imageView);
            }
        }
        // 错误默认图片
        else {
            if (defaultImage != 0) {
                imageView.setImageResource(defaultImage);
            }
        }
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                Glide.get(APP.getInstance().getApplicationContext()).clearMemory();
                Glide.get(APP.getInstance().getApplicationContext()).clearDiskCache();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(APP.getInstance().getApplicationContext(), "清除缓存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(APP.getInstance().getApplicationContext(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 清除缓存
     * 必须分线程执行
     */
    public static String getCachePath(final String imgUrl) {

        final String[] imagePathSd = {null};

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                try {

                    FutureTarget<File> future = Glide.with(APP.getInstance().getApplicationContext())
                            .load(imgUrl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); // 不压缩图片

                    File cacheFile = future.get();
                    subscriber.onNext(cacheFile.getAbsolutePath());
                } catch (Exception e) {
                    LogUtil.e("kalu", e.getMessage(), e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("iiiii", "cachePath = " + s);
                    }
                });

        return imagePathSd[0];
    }

    /**
     * 保存文件到图库
     *
     * @param imgUrl 图片网络url
     */
    public static void saveImageToGallery(final String imgUrl) {


        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                try {

                    FutureTarget<File> future = Glide.with(APP.getInstance().getApplicationContext())
                            .load(imgUrl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); // 不压缩图片

                    File cacheFile = future.get();

                    // 把文件插入到系统图库
                    ContentResolver contentResolver = APP.getInstance().getApplicationContext().getContentResolver();
                    String imageName = MediaStore.Images.Media.insertImage(contentResolver, cacheFile.getAbsolutePath(), cacheFile.getName(), null);

                    subscriber.onNext(imageName);
                } catch (Exception e) {
                    LogUtil.e("kalu", e.getMessage(), e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                        // 分线程toast
                        // Looper.prepare();
                        Toast.makeText(APP.getInstance().getApplicationContext(), "保存到系统相册成功" + s, Toast.LENGTH_SHORT).show();
                        // Looper.loop();
                    }
                });
    }
}