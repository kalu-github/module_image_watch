package com.demo.photo.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.demo.photo.util.LogUtil;

/**
 * Created by kalu on 2017/12/23.
 */

public class PhotoLoadView extends View {

    private final Paint mPaint = new Paint();
    private final RectF mRectF = new RectF();
    private float sweepAngle;

    public PhotoLoadView(Context context) {
        super(context);
    }

    public PhotoLoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoLoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhotoLoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画进度条
        if (sweepAngle == 0 || sweepAngle == 360) {
            mPaint.reset();
            mPaint.clearShadowLayer();
            // mPaint.setColor(Color.TRANSPARENT);
            return;
        }

        // 1. 画灰色背景
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 120, mPaint);

        // 2. 画黑色圆环背景
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 100, mPaint);

        // 3. 画蓝色圆环进度条
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        float width = (getWidth() / 2f);
        float height = (getHeight() / 2f);
        mRectF.set(width - 100, height - 100, width + 100, height + 100);
        canvas.drawArc(mRectF, -90, sweepAngle, true, mPaint);
    }

    public void setLoadProgress(long progress) {

        sweepAngle = progress * 360 / 100;
        LogUtil.e("kalu", "sweepAngle = " + sweepAngle + "progress = " + progress);

        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
