package com.demo.photo.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * description: 图片加载工具类
 * created by kalu on 2016/11/19 11:58
 */
public class GlideUtil {

    private static final String TAG = GlideUtil.class.getSimpleName();

    private static final int defaultImage = 0;

    // 条件设置
    // DiskCacheStrategy.NONE  不缓存文件
    // DiskCacheStrategy.RESOURCE  只缓存原图
    // DiskCacheStrategy.RESULT  只缓存最终加载的图（默认的缓存策略）
    // DiskCacheStrategy.ALL  同时缓存原图和结果图
    private static final RequestOptions options = new RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    /**
     * Glide.with()方法中尽量传入Activity或Fragment，而不是Application，不然没办法进行生命周期管理。
     */
    public static void loadImageSimple(Context context, ImageView imageView, String url) {
//        String style = "!app_boot1";
        String style = "";
        loadImage(context, imageView, url, style, 0, false, false);
    }

    public static void loadImageSimple(Context context, ImageView imageView, int resId) {
        loadImage(context, imageView, resId, 0, false, false);
    }

    public static void loadImageCircle(Context context, ImageView imageView, String url) {
//        String style = "!app_boot1";
        String style = "";
        loadImage(context, imageView, url, style, 0, true, false);
    }

    public static void loadImagePhoto(Context context, ImageView imageView, String url) {
//        String style = "!app_boot1";
        String style = "";
        loadImage(context, imageView, url, style, 0, false, true);
    }


    /**
     * @param imageView
     * @param url
     * @param style        图片cdn前缀
     * @param defaultImage 默认占位图
     * @param isCircle     圆形图片处理
     */
    private static void loadImage(Context context, ImageView imageView, String url, String style, int defaultImage, boolean isCircle, boolean isZip) {

        // 网络图片
        if (url != null && url.length() > 0) {
            if (url.startsWith("https://img.cdn.")) {
                url += style;
            }

            RequestBuilder builder = Glide
                    .with(context)
                    .load(url);

            if (defaultImage != 0) {
                options.placeholder(defaultImage)
                        .error(defaultImage);
            }

            if (isCircle) {
                options.circleCrop();
            } else if (isZip) {
                options.centerInside();
            } else {
                options.centerCrop();
            }


            builder.apply(options).into(imageView);
        }
        // 错误默认图片
        else {
            if (defaultImage != 0) {
                imageView.setImageResource(defaultImage);
            }
        }
    }

    /**
     * @param imageView
     * @param defaultImage 默认占位图
     * @param isCircle     圆形图片处理
     */
    private static void loadImage(Context context, ImageView imageView, int resId, int defaultImage, boolean isCircle, boolean isZip) {


        RequestBuilder builder = Glide
                .with(context)
                .load(resId);

        if (defaultImage != 0) {
            options.placeholder(defaultImage)
                    .error(defaultImage);
        }

        if (isCircle) {
            options.circleCrop();
        } else if (isZip) {
            options.centerInside();
        } else {
            options.centerCrop();
        }
        builder.apply(options).into(imageView);
    }

//    /**
//     * 清除缓存
//     */
//    public static void clearCache() {
//
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(BaseApplication.getInstance().getApplicationContext()).clearMemory();
//                Glide.get(BaseApplication.getInstance().getApplicationContext()).clearDiskCache();
//            }
//        });
//    }

    /**
     * 清除缓存
     * 必须分线程执行
     */
//    public static String getCachePath(String imgUrl) {
//
//        String result = null;
//
//        try {
//
//            FutureTarget<File> future = Glide.with(BaseApplication.getInstance().getApplicationContext())
//                    .load(imgUrl)
//                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); // 不压缩图片
//
//            File cacheFile = future.get();
//            result = cacheFile.getAbsolutePath();
//        } catch (Exception e) {
//            LogUtil.e(TAG, e.getMessage(), e);
//        }
//
//        return result;
//    }
}
