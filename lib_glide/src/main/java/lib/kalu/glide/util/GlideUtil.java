package lib.kalu.glide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import lib.kalu.bitmap.IntensifyImageView;

/**
 * description: 图片加载工具类
 * created by kalu on 2016/11/19 11:58
 */
public class GlideUtil {

    private static final String TAG = GlideUtil.class.getSimpleName();

    private static final int defaultImage = -1;

    // 条件设置
    // DiskCacheStrategy.NONE  不缓存文件
    // DiskCacheStrategy.RESOURCE  只缓存原图
    // DiskCacheStrategy.RESULT  只缓存最终加载的图（默认的缓存策略）
    // DiskCacheStrategy.ALL  同时缓存原图和结果图
    private static final RequestOptions options = new RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static void loadImage(Context context, ImageView image, String url) {
        loadImage(context, image, url, -1, -1, false, false);
    }

//    public static void loadLarge(Context context, IntensifyImageView image, String url) {
//        loadLarge(context, image, url, -1);
//    }

    private static void loadImage(Context context, ImageView image, String url, int placeholder, int scaletype, boolean isCircle, boolean isZip) {

        if (null == context || null == image)
            return;

        if (null == url || url.length() == 0)
            return;

        if (!url.startsWith("http")) {
            image.setImageURI(Uri.fromFile(new File(url)));
            return;
        }

        final RequestBuilder builder = Glide
                .with(context)
                .load(url);
        if (placeholder != -1) {
            options.placeholder(placeholder)
                    .error(placeholder)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        } else if (defaultImage != -1) {
            options.placeholder(defaultImage)
                    .error(defaultImage)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        if (isCircle) {
            options.circleCrop();
        } else if (isZip) {
            options.centerInside();
        } else {
            options.centerInside();
        }

        builder.apply(options).into(image);
//        builder.apply(options).into(image);
////            builder.apply(options).into(new SimpleTarget<BitmapDrawable>() {
////                @Override
////                public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super Bitmap> transition) {
////                    image.setImageBitmap(resource);
////                }
////            });
    }

//    private static void loadLarge(Context context, IntensifyImageView image, String url, int placeholder) {
//
//        if (null == context || null == image)
//            return;
//
//        if (null == url || url.length() == 0)
//            return;
//
//        if (!url.startsWith("http")) {
//            try {
//                File file = new File(url);
//                image.setImage(file);
//            } catch (Exception e) {
//                Log.e("kalu", e.getMessage(), e);
//            }
//            return;
//        }
//
//        final RequestBuilder builder = Glide
//                .with(context)
//                .load(url);
//        if (placeholder != -1) {
//            options.placeholder(placeholder)
//                    .error(placeholder)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        } else if (defaultImage != -1) {
//            options.placeholder(defaultImage)
//                    .error(defaultImage)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        }
//
//        options.dontAnimate();
//        options.dontTransform();
//
//        builder.apply(options).into(new SimpleTarget<BitmapDrawable>() {
//            @Override
//            public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
//
//                int width = resource.getIntrinsicWidth();
//                int height = resource.getIntrinsicHeight();
//                int opacity = resource.getOpacity();
//                Bitmap bitmap = Bitmap.createBitmap(width, height, opacity != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                InputStream is = new ByteArrayInputStream(baos.toByteArray());
//
//                image.setImage(is);
//            }
//        });
//    }

    public static void clear(Context context) {

        new Thread(() -> {
            Glide.get(context).clearMemory();
            Glide.get(context).clearDiskCache();
        }).start();
    }

    public static String getPath(Context context, String url) {

        try {

            final FutureTarget<File> future = Glide.with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL); // 不压缩图片
            final File cacheFile = future.get();
            return cacheFile.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}
