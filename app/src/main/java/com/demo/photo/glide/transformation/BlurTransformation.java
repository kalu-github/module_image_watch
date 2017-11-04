package com.demo.photo.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.demo.photo.APP;

import java.security.MessageDigest;

public class BlurTransformation implements Transformation<Bitmap> {

    private static int MAX_RADIUS = 99;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private BitmapPool mBitmapPool;

    private int mRadius;
    private int mSampling;

    public BlurTransformation() {
        this(Glide.get(APP.getInstance().getApplicationContext()).getBitmapPool(), MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(BitmapPool pool) {
        this(pool, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(BitmapPool pool, int radius) {
        this(pool, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius) {
        this(Glide.get(APP.getInstance().getApplicationContext()).getBitmapPool(), radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius, int sampling) {
        this(Glide.get(APP.getInstance().getApplicationContext()).getBitmapPool(), radius, sampling);
    }

    public BlurTransformation(BitmapPool pool, int radius, int sampling) {
        mBitmapPool = pool;
        mRadius = radius;
        mSampling = sampling;
    }

    @Override
    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();
        int scaledWidth = width / mSampling;
        int scaledHeight = height / mSampling;

        Bitmap bitmap = mBitmapPool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = RSBlur.blur(APP.getInstance().getApplicationContext(), bitmap, mRadius);
            } catch (RSRuntimeException e) {
                bitmap = FastBlur.blur(bitmap, mRadius, true);
            }
        } else {
            bitmap = FastBlur.blur(bitmap, mRadius, true);
        }

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}