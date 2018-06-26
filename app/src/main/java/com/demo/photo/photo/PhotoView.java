package com.demo.photo.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * description: 旋转, 缩放, 平移
 * created by kalu on 2018/4/19 16:33
 */
final class PhotoView extends AppCompatImageView {

    /*************************************************************************/

    private static final int MAX_DEGREES_STEP = 120;
    // 平移, 动画时长
    private final int ANIM_OTHER = 340;
    // 旋转, 动画时长
    private final float ANIM_ROTATE = 50f;
    // 缩放, 最大倍数
    private final float SCALE_MAX = 5f;
    // 缩放, 最小倍数
    private final float SCALE_MIN = 1f;
    // 缩放, 当前倍数
    private float mScaleMultiple = 1f;
    private int MAX_FLING_OVER_SCROLL = (int) (getResources().getDisplayMetrics().density * 30);
    private int MAX_OVER_RESISTANCE = (int) (getResources().getDisplayMetrics().density * 140);
    private Matrix mBaseMatrix = new Matrix();
    private Matrix mAnimaMatrix = new Matrix();
    private Matrix mSynthesisMatrix = new Matrix();
    private Matrix mTmpMatrix = new Matrix();
    private boolean hasMultiTouch;
    private boolean hasDrawable;
    private boolean isKnowSize;
    private boolean hasOverTranslate;
    private boolean mAdjustViewBounds;
    // 当前是否处于放大状态
    private boolean isZoonUp;
    private boolean canRotate;
    private boolean imgLargeWidth;
    private boolean imgLargeHeight;
    private float mRotateFlag;
    private int mRoateAngle;  // 旋转角度
    private int mTranslateX;
    private int mTranslateY;
    private float mHalfBaseRectWidth;
    private float mHalfBaseRectHeight;
    private RectF mWidgetRect = new RectF();
    private RectF mBaseRect = new RectF();
    private RectF mImgRect = new RectF();
    private RectF mTmpRect = new RectF();
    private RectF mCommonRect = new RectF();
    private PointF mScreenCenter = new PointF();
    private PointF mScaleCenter = new PointF();
    private PointF mRotateCenter = new PointF();
    private Transform mTranslate = new Transform();
    private RectF mClip;
    private Runnable mCompleteCallBack;
    private float mPrevSlope;
    private float mCurrSlope;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;

            mScaleMultiple *= scaleFactor;
            mAnimaMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            executeTranslate();
            return true;
        }
    });
    /*****************************************************************/

    private OnPhotoChangeListener mListener;
    private final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onDown(MotionEvent e) {
            hasOverTranslate = false;
            hasMultiTouch = false;
            canRotate = false;
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasMultiTouch) return false;
            if (!imgLargeWidth && !imgLargeHeight) return false;
            if (mTranslate.isRuning) return false;

            float vx = velocityX;
            float vy = velocityY;

            if (Math.round(mImgRect.left) >= mWidgetRect.left || Math.round(mImgRect.right) <= mWidgetRect.right) {
                vx = 0;
            }

            if (Math.round(mImgRect.top) >= mWidgetRect.top || Math.round(mImgRect.bottom) <= mWidgetRect.bottom) {
                vy = 0;
            }

            if (canRotate || mRoateAngle % 90 != 0) {

                int toDegrees = (mRoateAngle / 90) * 90;
                float remainder = mRoateAngle % 90;

                if (remainder > 45)
                    toDegrees += 90;
                else if (remainder < -45)
                    toDegrees -= 90;

                mTranslate.withRotate(mRoateAngle, toDegrees);

                mRoateAngle = toDegrees;

                //LogUtil.e("zhangh", "onRotate22 ==> mRoateAngle = " + mRoateAngle);
                if (null != mListener) {
                    mListener.onRotate(mRoateAngle, 0, 0);
                }
            }

            doTranslateReset(mImgRect);
            mTranslate.withFling(vx, vy);

            mTranslate.start();
//            onUp(e2);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mTranslate.isRuning) {
                mTranslate.stop();
            }

            if (canScrollHorizontallySelf(distanceX)) {
                if (distanceX < 0 && mImgRect.left - distanceX > mWidgetRect.left)
                    distanceX = mImgRect.left;
                if (distanceX > 0 && mImgRect.right - distanceX < mWidgetRect.right)
                    distanceX = mImgRect.right - mWidgetRect.right;

                mAnimaMatrix.postTranslate(-distanceX, 0);
                mTranslateX -= distanceX;
            } else if (imgLargeWidth || hasMultiTouch || hasOverTranslate) {
                checkRect();
                if (!hasMultiTouch) {
                    if (distanceX < 0 && mImgRect.left - distanceX > mCommonRect.left)
                        distanceX = resistanceScrollByX(mImgRect.left - mCommonRect.left, distanceX);
                    if (distanceX > 0 && mImgRect.right - distanceX < mCommonRect.right)
                        distanceX = resistanceScrollByX(mImgRect.right - mCommonRect.right, distanceX);
                }

                mTranslateX -= distanceX;
                mAnimaMatrix.postTranslate(-distanceX, 0);
                hasOverTranslate = true;
            }

            if (canScrollVerticallySelf(distanceY)) {
                if (distanceY < 0 && mImgRect.top - distanceY > mWidgetRect.top)
                    distanceY = mImgRect.top;
                if (distanceY > 0 && mImgRect.bottom - distanceY < mWidgetRect.bottom)
                    distanceY = mImgRect.bottom - mWidgetRect.bottom;

                mAnimaMatrix.postTranslate(0, -distanceY);
                mTranslateY -= distanceY;
            } else if (imgLargeHeight || hasOverTranslate || hasMultiTouch) {
                checkRect();
                if (!hasMultiTouch) {
                    if (distanceY < 0 && mImgRect.top - distanceY > mCommonRect.top)
                        distanceY = resistanceScrollByY(mImgRect.top - mCommonRect.top, distanceY);
                    if (distanceY > 0 && mImgRect.bottom - distanceY < mCommonRect.bottom)
                        distanceY = resistanceScrollByY(mImgRect.bottom - mCommonRect.bottom, distanceY);
                }

                mAnimaMatrix.postTranslate(0, -distanceY);
                mTranslateY -= distanceY;
                hasOverTranslate = true;
            }

            executeTranslate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            mTranslate.stop();

            float from, to;
            float imgcx = mImgRect.left + mImgRect.width() / 2;
            float imgcy = mImgRect.top + mImgRect.height() / 2;

            mScaleCenter.set(imgcx, imgcy);
            mRotateCenter.set(imgcx, imgcy);
            mTranslateX = 0;
            mTranslateY = 0;

            if (isZoonUp) {
                from = mScaleMultiple;
                to = SCALE_MIN;
            } else {
                from = mScaleMultiple;
                to = SCALE_MAX;

                mScaleCenter.set(e.getX(), e.getY());
            }

            mTmpMatrix.reset();
            mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
            mTmpMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y);
            mTmpMatrix.postTranslate(-mHalfBaseRectWidth, -mHalfBaseRectHeight);
            mTmpMatrix.postRotate(mRoateAngle, mRotateCenter.x, mRotateCenter.y);
            mTmpMatrix.postScale(to, to, mScaleCenter.x, mScaleCenter.y);
            mTmpMatrix.postTranslate(mTranslateX, mTranslateY);
            mTmpMatrix.mapRect(mTmpRect, mBaseRect);
            doTranslateReset(mTmpRect);

            isZoonUp = !isZoonUp;
            mTranslate.withScale(from, to);
            mTranslate.start();

            return false;
        }
    });

    /**************************************************************************/

    public PhotoView(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    private static int getDrawableWidth(Drawable d) {
        int width = d.getIntrinsicWidth();
        if (width <= 0) width = d.getMinimumWidth();
        if (width <= 0) width = d.getBounds().width();
        return width;
    }

    private static int getDrawableHeight(Drawable d) {
        int height = d.getIntrinsicHeight();
        if (height <= 0) height = d.getMinimumHeight();
        if (height <= 0) height = d.getBounds().height();
        return height;
    }

    private float caculateSlope(MotionEvent event) {
        x1 = event.getX(0);
        y1 = event.getY(0);
        x2 = event.getX(1);
        y2 = event.getY(1);
        return (y2 - y1) / (x2 - x1);
    }

    /*************************************************************************/

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int Action = event.getActionMasked();
        if (event.getPointerCount() >= 2) hasMultiTouch = true;

        mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) mPrevSlope = caculateSlope(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    mCurrSlope = caculateSlope(event);

                    double currDegrees = Math.toDegrees(Math.atan(mCurrSlope));
                    double prevDegrees = Math.toDegrees(Math.atan(mPrevSlope));

                    double deltaSlope = currDegrees - prevDegrees;

                    if (Math.abs(deltaSlope) <= MAX_DEGREES_STEP) {

                        float focusX = (x2 + x1) / 2;
                        float focusY = (y2 + y1) / 2;

                        mRotateFlag += (float) deltaSlope;
                        if (canRotate) {
                            mRoateAngle += (float) deltaSlope;
                            mAnimaMatrix.postRotate((float) deltaSlope, focusX, focusY);
                        } else {
                            if (Math.abs(mRotateFlag) >= ANIM_ROTATE) {
                                canRotate = true;
                                mRotateFlag = 0;
                            }
                        }
                    }
                    mPrevSlope = mCurrSlope;
                }
                break;
        }

        mScaleGestureDetector.onTouchEvent(event);

        if (Action == MotionEvent.ACTION_UP || Action == MotionEvent.ACTION_CANCEL) onUp();
        return true;
    }

    /**************************************************************************/

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        //  LogUtil.e("tyu", "setScaleType ==>");
        super.setScaleType(scaleType);
        initBase();
    }

    @Override
    public void setImageResource(int resId) {
        // LogUtil.e("tyu", "setImageResource ==>");
        Drawable drawable = getResources().getDrawable(resId);
        setImageDrawable(drawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        //   LogUtil.e("tyu", "setImageDrawable ==>");
        super.setImageDrawable(drawable);

        if (drawable == null) {
            hasDrawable = false;
            return;
        }

        if (!hasSize(drawable))
            return;

        if (!hasDrawable) {
            hasDrawable = true;
        }

        initBase();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //LogUtil.e("tyu", "onMeasure ==>");
        if (!hasDrawable) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        Drawable d = getDrawable();
        int drawableW = getDrawableWidth(d);
        int drawableH = getDrawableHeight(d);

        int pWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int pHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int width, height;
        ViewGroup.LayoutParams p = getLayoutParams();

        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (p.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (widthMode == View.MeasureSpec.UNSPECIFIED) {
                width = drawableW;
            } else {
                width = pWidth;
            }
        } else {
            if (widthMode == View.MeasureSpec.EXACTLY) {
                width = pWidth;
            } else if (widthMode == View.MeasureSpec.AT_MOST) {
                width = drawableW > pWidth ? pWidth : drawableW;
            } else {
                width = drawableW;
            }
        }

        if (p.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                height = drawableH;
            } else {
                height = pHeight;
            }
        } else {
            if (heightMode == View.MeasureSpec.EXACTLY) {
                height = pHeight;
            } else if (heightMode == View.MeasureSpec.AT_MOST) {
                height = drawableH > pHeight ? pHeight : drawableH;
            } else {
                height = drawableH;
            }
        }

        if (mAdjustViewBounds && (float) drawableW / drawableH != (float) width / height) {

            float hScale = (float) height / drawableH;
            float wScale = (float) width / drawableW;

            float scale = hScale < wScale ? hScale : wScale;
            width = p.width == ViewGroup.LayoutParams.MATCH_PARENT ? width : (int) (drawableW * scale);
            height = p.height == ViewGroup.LayoutParams.MATCH_PARENT ? height : (int) (drawableH * scale);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        // LogUtil.e("tyu", "setAdjustViewBounds ==>");
        super.setAdjustViewBounds(adjustViewBounds);
        mAdjustViewBounds = adjustViewBounds;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //  LogUtil.e("tyu", "onSizeChanged ==>");
        super.onSizeChanged(w, h, oldw, oldh);

        mWidgetRect.set(0, 0, w, h);
        mScreenCenter.set(w / 2, h / 2);

        if (!isKnowSize) {
            isKnowSize = true;
            initBase();
        }
    }

    /**************************************************************************/

    private boolean hasSize(Drawable d) {
        if ((d.getIntrinsicHeight() <= 0 || d.getIntrinsicWidth() <= 0)
                && (d.getMinimumWidth() <= 0 || d.getMinimumHeight() <= 0)
                && (d.getBounds().width() <= 0 || d.getBounds().height() <= 0)) {
            return false;
        }
        return true;
    }

    private void initBase() {
        if (!hasDrawable) return;
        if (!isKnowSize) return;

        mBaseMatrix.reset();
        mAnimaMatrix.reset();

        isZoonUp = false;

        Drawable img = getDrawable();

        int w = getWidth();
        int h = getHeight();
        int imgw = getDrawableWidth(img);
        int imgh = getDrawableHeight(img);

        mBaseRect.set(0, 0, imgw, imgh);

        // 以图片中心点居中位移
        int tx = (w - imgw) / 2;
        int ty = (h - imgh) / 2;

        float sx = 1;
        float sy = 1;

        // 缩放，默认不超过屏幕大小
        if (imgw > w) {
            sx = (float) w / imgw;
        }

        if (imgh > h) {
            sy = (float) h / imgh;
        }

        float scale = sx < sy ? sx : sy;

        mBaseMatrix.reset();
        mBaseMatrix.postTranslate(tx, ty);
        mBaseMatrix.postScale(scale, scale, mScreenCenter.x, mScreenCenter.y);
        mBaseMatrix.mapRect(mBaseRect);

        mHalfBaseRectWidth = mBaseRect.width() / 2;
        mHalfBaseRectHeight = mBaseRect.height() / 2;

        mScaleCenter.set(mScreenCenter);
        mRotateCenter.set(mScaleCenter);

        executeTranslate();

        switch (getScaleType()) {
            case CENTER:
                initCenter();
                break;
            case CENTER_CROP:
                initCenterCrop();
                break;
            case CENTER_INSIDE:
                initCenterInside();
                break;
            case FIT_CENTER:
                initFitCenter();
                break;
            case FIT_START:
                initFitStart();
                break;
            case FIT_END:
                initFitEnd();
                break;
            case FIT_XY:
                initFitXY();
                break;
            default:
                initFitCenter();
                break;
        }
    }

    private void initCenter() {
        if (!hasDrawable) return;
        if (!isKnowSize) return;

        Drawable img = getDrawable();

        int imgw = getDrawableWidth(img);
        int imgh = getDrawableHeight(img);

        if (imgw > mWidgetRect.width() || imgh > mWidgetRect.height()) {
            float scaleX = imgw / mImgRect.width();
            float scaleY = imgh / mImgRect.height();

            mScaleMultiple = scaleX > scaleY ? scaleX : scaleY;
            mAnimaMatrix.postScale(mScaleMultiple, mScaleMultiple, mScreenCenter.x, mScreenCenter.y);

            executeTranslate();
            resetBase();
        }
    }

    private void initCenterCrop() {
        if (mImgRect.width() < mWidgetRect.width() || mImgRect.height() < mWidgetRect.height()) {
            float scaleX = mWidgetRect.width() / mImgRect.width();
            float scaleY = mWidgetRect.height() / mImgRect.height();

            mScaleMultiple = scaleX > scaleY ? scaleX : scaleY;
            mAnimaMatrix.postScale(mScaleMultiple, mScaleMultiple, mScreenCenter.x, mScreenCenter.y);

            executeTranslate();
            resetBase();
        }
    }

    private void initCenterInside() {
        if (mImgRect.width() > mWidgetRect.width() || mImgRect.height() > mWidgetRect.height()) {
            float scaleX = mWidgetRect.width() / mImgRect.width();
            float scaleY = mWidgetRect.height() / mImgRect.height();

            mScaleMultiple = scaleX < scaleY ? scaleX : scaleY;
            mAnimaMatrix.postScale(mScaleMultiple, mScaleMultiple, mScreenCenter.x, mScreenCenter.y);

            executeTranslate();
            resetBase();
        }
    }

    private void initFitCenter() {
        if (mImgRect.width() < mWidgetRect.width()) {
            mScaleMultiple = mWidgetRect.width() / mImgRect.width();
            mAnimaMatrix.postScale(mScaleMultiple, mScaleMultiple, mScreenCenter.x, mScreenCenter.y);

            executeTranslate();
            resetBase();
        }
    }

    private void initFitStart() {
        initFitCenter();

        float ty = -mImgRect.top;
        mTranslateY += ty;
        mAnimaMatrix.postTranslate(0, ty);
        executeTranslate();
        resetBase();
    }

    private void initFitEnd() {
        initFitCenter();

        float ty = (mWidgetRect.bottom - mImgRect.bottom);
        mTranslateY += ty;
        mAnimaMatrix.postTranslate(0, ty);
        executeTranslate();
        resetBase();
    }

    private void initFitXY() {
        float scaleX = mWidgetRect.width() / mImgRect.width();
        float scaleY = mWidgetRect.height() / mImgRect.height();

        mAnimaMatrix.postScale(scaleX, scaleY, mScreenCenter.x, mScreenCenter.y);

        executeTranslate();
        resetBase();
    }

    private void resetBase() {
        Drawable img = getDrawable();
        int imgw = getDrawableWidth(img);
        int imgh = getDrawableHeight(img);
        mBaseRect.set(0, 0, imgw, imgh);
        mBaseMatrix.set(mSynthesisMatrix);
        mBaseMatrix.mapRect(mBaseRect);
        mHalfBaseRectWidth = mBaseRect.width() / 2;
        mHalfBaseRectHeight = mBaseRect.height() / 2;
        mScaleMultiple = SCALE_MIN;
        mTranslateX = 0;
        mTranslateY = 0;
        mAnimaMatrix.reset();
    }

    private void executeTranslate() {
        mSynthesisMatrix.set(mBaseMatrix);
        mSynthesisMatrix.postConcat(mAnimaMatrix);
        setImageMatrix(mSynthesisMatrix);

        mAnimaMatrix.mapRect(mImgRect, mBaseRect);

        imgLargeWidth = mImgRect.width() > mWidgetRect.width();
        imgLargeHeight = mImgRect.height() > mWidgetRect.height();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mClip != null) {
            canvas.clipRect(mClip);
            mClip = null;
        }
        super.draw(canvas);
    }

    private void onUp() {
        if (mTranslate.isRuning) return;

        if (canRotate || mRoateAngle % 90 != 0) {
            int toDegrees = (mRoateAngle / 90) * 90;
            int remainder = mRoateAngle % 90;

            if (remainder > 45)
                toDegrees += 90;
            else if (remainder < -45)
                toDegrees -= 90;

            mTranslate.withRotate(mRoateAngle, toDegrees);
            mRoateAngle = toDegrees;
            //  LogUtil.e("zhangh", "onRotate22 ==> mRoateAngle = " + mRoateAngle);
            if (null != mListener) {
                mListener.onRotate(mRoateAngle, 0, 0);
            }
        }

        if (mScaleMultiple < 1) {
            mScaleMultiple = SCALE_MIN;
            mTranslate.withScale(mScaleMultiple, 1);
        } else if (mScaleMultiple > SCALE_MAX) {
            mScaleMultiple = SCALE_MAX;
            mTranslate.withScale(mScaleMultiple, SCALE_MAX);
        }

        float cx = mImgRect.left + mImgRect.width() / 2;
        float cy = mImgRect.top + mImgRect.height() / 2;

        mScaleCenter.set(cx, cy);
        mRotateCenter.set(cx, cy);

        mTranslateX = 0;
        mTranslateY = 0;

        mTmpMatrix.reset();
        mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
        mTmpMatrix.postTranslate(cx - mHalfBaseRectWidth, cy - mHalfBaseRectHeight);
        mTmpMatrix.postScale(mScaleMultiple, mScaleMultiple, cx, cy);
        mTmpMatrix.postRotate(mRoateAngle, cx, cy);
        mTmpMatrix.mapRect(mTmpRect, mBaseRect);

        doTranslateReset(mTmpRect);
        mTranslate.start();
    }

    private void doTranslateReset(RectF imgRect) {
        int tx = 0;
        int ty = 0;

        if (imgRect.width() <= mWidgetRect.width()) {
            if (!isImageCenterWidth(imgRect))
                tx = -(int) ((mWidgetRect.width() - imgRect.width()) / 2 - imgRect.left);
        } else {
            if (imgRect.left > mWidgetRect.left) {
                tx = (int) (imgRect.left - mWidgetRect.left);
            } else if (imgRect.right < mWidgetRect.right) {
                tx = (int) (imgRect.right - mWidgetRect.right);
            }
        }

        if (imgRect.height() <= mWidgetRect.height()) {
            if (!isImageCenterHeight(imgRect))
                ty = -(int) ((mWidgetRect.height() - imgRect.height()) / 2 - imgRect.top);
        } else {
            if (imgRect.top > mWidgetRect.top) {
                ty = (int) (imgRect.top - mWidgetRect.top);
            } else if (imgRect.bottom < mWidgetRect.bottom) {
                ty = (int) (imgRect.bottom - mWidgetRect.bottom);
            }
        }

        if (tx != 0 || ty != 0) {
            if (!mTranslate.mFlingScroller.isFinished()) mTranslate.mFlingScroller.abortAnimation();
            mTranslate.withTranslate(mTranslateX, mTranslateY, -tx, -ty);
        }
    }

    private boolean isImageCenterHeight(RectF rect) {
        return Math.abs(Math.round(rect.top) - (mWidgetRect.height() - rect.height()) / 2) < 1;
    }

    private boolean isImageCenterWidth(RectF rect) {
        return Math.abs(Math.round(rect.left) - (mWidgetRect.width() - rect.width()) / 2) < 1;
    }

    private float resistanceScrollByX(float overScroll, float detalX) {
        float s = detalX * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / (float) MAX_OVER_RESISTANCE);
        return s;
    }

    private float resistanceScrollByY(float overScroll, float detalY) {
        float s = detalY * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / (float) MAX_OVER_RESISTANCE);
        return s;
    }

    /**
     * 匹配两个Rect的共同部分输出到out，若无共同部分则输出0，0，0，0
     */
    private void mapRect(RectF r1, RectF r2, RectF out) {

        float l, r, t, b;

        l = r1.left > r2.left ? r1.left : r2.left;
        r = r1.right < r2.right ? r1.right : r2.right;

        if (l > r) {
            out.set(0, 0, 0, 0);
            return;
        }

        t = r1.top > r2.top ? r1.top : r2.top;
        b = r1.bottom < r2.bottom ? r1.bottom : r2.bottom;

        if (t > b) {
            out.set(0, 0, 0, 0);
            return;
        }

        out.set(l, t, r, b);
    }

    private void checkRect() {
        if (!hasOverTranslate) {
            mapRect(mWidgetRect, mImgRect, mCommonRect);
        }
    }

    public boolean canScrollHorizontallySelf(float direction) {
        if (mImgRect.width() <= mWidgetRect.width()) return false;
        if (direction < 0 && Math.round(mImgRect.left) - direction >= mWidgetRect.left)
            return false;
        if (direction > 0 && Math.round(mImgRect.right) - direction <= mWidgetRect.right)
            return false;
        return true;
    }

    public boolean canScrollVerticallySelf(float direction) {
        if (mImgRect.height() <= mWidgetRect.height()) return false;
        if (direction < 0 && Math.round(mImgRect.top) - direction >= mWidgetRect.top)
            return false;
        if (direction > 0 && Math.round(mImgRect.bottom) - direction <= mWidgetRect.bottom)
            return false;
        return true;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (hasMultiTouch) return true;
        return canScrollHorizontallySelf(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (hasMultiTouch) return true;
        return canScrollVerticallySelf(direction);
    }

    public void setOnPhotoChangeListener(OnPhotoChangeListener listener) {
        mListener = listener;
    }

    public interface ClipCalculate {
        float calculateTop();
    }

    private class InterpolatorProxy implements Interpolator {

        private Interpolator mTarget;

        private InterpolatorProxy() {
            mTarget = new DecelerateInterpolator();
        }

        public void setTargetInterpolator(Interpolator interpolator) {
            mTarget = interpolator;
        }

        @Override
        public float getInterpolation(float input) {
            if (mTarget != null) {
                return mTarget.getInterpolation(input);
            }
            return input;
        }
    }

    private class Transform implements Runnable {

        boolean isRuning;

        OverScroller mTranslateScroller;
        OverScroller mFlingScroller;
        Scroller mScaleScroller;
        Scroller mClipScroller;
        Scroller mRotateScroller;

        ClipCalculate C;

        int mLastFlingX;
        int mLastFlingY;

        int mLastTranslateX;
        int mLastTranslateY;

        RectF mClipRect = new RectF();

        InterpolatorProxy mInterpolatorProxy = new InterpolatorProxy();

        Transform() {
            Context ctx = getContext();
            mTranslateScroller = new OverScroller(ctx, mInterpolatorProxy);
            mScaleScroller = new Scroller(ctx, mInterpolatorProxy);
            mFlingScroller = new OverScroller(ctx, mInterpolatorProxy);
            mClipScroller = new Scroller(ctx, mInterpolatorProxy);
            mRotateScroller = new Scroller(ctx, mInterpolatorProxy);
        }

        public void setInterpolator(Interpolator interpolator) {
            mInterpolatorProxy.setTargetInterpolator(interpolator);
        }

        void withTranslate(int startX, int startY, int deltaX, int deltaY) {
            mLastTranslateX = 0;
            mLastTranslateY = 0;
            mTranslateScroller.startScroll(0, 0, deltaX, deltaY, ANIM_OTHER);
        }

        void withScale(float form, float to) {
            mScaleScroller.startScroll((int) (form * 10000), 0, (int) ((to - form) * 10000), 0, ANIM_OTHER);
        }

        void withClip(float fromX, float fromY, float deltaX, float deltaY, int d, ClipCalculate c) {
            mClipScroller.startScroll((int) (fromX * 10000), (int) (fromY * 10000), (int) (deltaX * 10000), (int) (deltaY * 10000), d);
            C = c;
        }

        void withRotate(int fromDegrees, int toDegrees) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, ANIM_OTHER);
        }

        void withRotate(int fromDegrees, int toDegrees, int during) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, during);
        }

        void withFling(float velocityX, float velocityY) {
            mLastFlingX = velocityX < 0 ? Integer.MAX_VALUE : 0;
            int distanceX = (int) (velocityX > 0 ? Math.abs(mImgRect.left) : mImgRect.right - mWidgetRect.right);
            distanceX = velocityX < 0 ? Integer.MAX_VALUE - distanceX : distanceX;
            int minX = velocityX < 0 ? distanceX : 0;
            int maxX = velocityX < 0 ? Integer.MAX_VALUE : distanceX;
            int overX = velocityX < 0 ? Integer.MAX_VALUE - minX : distanceX;

            mLastFlingY = velocityY < 0 ? Integer.MAX_VALUE : 0;
            int distanceY = (int) (velocityY > 0 ? Math.abs(mImgRect.top) : mImgRect.bottom - mWidgetRect.bottom);
            distanceY = velocityY < 0 ? Integer.MAX_VALUE - distanceY : distanceY;
            int minY = velocityY < 0 ? distanceY : 0;
            int maxY = velocityY < 0 ? Integer.MAX_VALUE : distanceY;
            int overY = velocityY < 0 ? Integer.MAX_VALUE - minY : distanceY;

            if (velocityX == 0) {
                maxX = 0;
                minX = 0;
            }

            if (velocityY == 0) {
                maxY = 0;
                minY = 0;
            }

            mFlingScroller.fling(mLastFlingX, mLastFlingY, (int) velocityX, (int) velocityY, minX, maxX, minY, maxY, Math.abs(overX) < MAX_FLING_OVER_SCROLL * 2 ? 0 : MAX_FLING_OVER_SCROLL, Math.abs(overY) < MAX_FLING_OVER_SCROLL * 2 ? 0 : MAX_FLING_OVER_SCROLL);
        }

        void start() {
            isRuning = true;
            postExecute();
        }

        void stop() {
            removeCallbacks(this);
            mTranslateScroller.abortAnimation();
            mScaleScroller.abortAnimation();
            mFlingScroller.abortAnimation();
            mRotateScroller.abortAnimation();
            isRuning = false;
        }

        @Override
        public void run() {

            // if (!isRuning) return;

            boolean endAnima = true;

            if (mScaleScroller.computeScrollOffset()) {
                mScaleMultiple = mScaleScroller.getCurrX() / 10000f;
                endAnima = false;
            }

            if (mTranslateScroller.computeScrollOffset()) {
                int tx = mTranslateScroller.getCurrX() - mLastTranslateX;
                int ty = mTranslateScroller.getCurrY() - mLastTranslateY;
                mTranslateX += tx;
                mTranslateY += ty;
                mLastTranslateX = mTranslateScroller.getCurrX();
                mLastTranslateY = mTranslateScroller.getCurrY();
                endAnima = false;
            }

            if (mFlingScroller.computeScrollOffset()) {
                int x = mFlingScroller.getCurrX() - mLastFlingX;
                int y = mFlingScroller.getCurrY() - mLastFlingY;

                mLastFlingX = mFlingScroller.getCurrX();
                mLastFlingY = mFlingScroller.getCurrY();

                mTranslateX += x;
                mTranslateY += y;
                endAnima = false;
            }

            if (mRotateScroller.computeScrollOffset()) {
                mRoateAngle = mRotateScroller.getCurrX();
                endAnima = false;
            }

            if (mClipScroller.computeScrollOffset() || mClip != null) {
                float sx = mClipScroller.getCurrX() / 10000f;
                float sy = mClipScroller.getCurrY() / 10000f;
                mTmpMatrix.setScale(sx, sy, (mImgRect.left + mImgRect.right) / 2, C.calculateTop());
                mTmpMatrix.mapRect(mClipRect, mImgRect);

                if (sx == 1) {
                    mClipRect.left = mWidgetRect.left;
                    mClipRect.right = mWidgetRect.right;
                }

                if (sy == 1) {
                    mClipRect.top = mWidgetRect.top;
                    mClipRect.bottom = mWidgetRect.bottom;
                }

                mClip = mClipRect;
            }

            if (!endAnima) {
                applyAnima();
                postExecute();
            } else {
                isRuning = false;

                // 修复动画结束后边距有些空隙，
                boolean needFix = false;

                if (imgLargeWidth) {
                    if (mImgRect.left > 0) {
                        mTranslateX -= mImgRect.left;
                    } else if (mImgRect.right < mWidgetRect.width()) {
                        mTranslateX -= (int) (mWidgetRect.width() - mImgRect.right);
                    }
                    needFix = true;
                }

                if (imgLargeHeight) {
                    if (mImgRect.top > 0) {
                        mTranslateY -= mImgRect.top;
                    } else if (mImgRect.bottom < mWidgetRect.height()) {
                        mTranslateY -= (int) (mWidgetRect.height() - mImgRect.bottom);
                    }
                    needFix = true;
                }

                if (needFix) {
                    applyAnima();
                }

                invalidate();

                if (mCompleteCallBack != null) {
                    mCompleteCallBack.run();
                    mCompleteCallBack = null;
                }
            }
        }

        private void applyAnima() {
            mAnimaMatrix.reset();
            mAnimaMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
            mAnimaMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y);
            mAnimaMatrix.postTranslate(-mHalfBaseRectWidth, -mHalfBaseRectHeight);
            mAnimaMatrix.postRotate(mRoateAngle, mRotateCenter.x, mRotateCenter.y);
            mAnimaMatrix.postScale(mScaleMultiple, mScaleMultiple, mScaleCenter.x, mScaleCenter.y);
            mAnimaMatrix.postTranslate(mTranslateX, mTranslateY);
            executeTranslate();
        }


        private void postExecute() {
            if (isRuning) post(this);
        }
    }

    /******************************************************/

    /**
     * 旋转图片
     *
     * @param clockwise 顺时针方向
     */
    public final void rotateImage(boolean clockwise) {

        if (null == mTranslate || mTranslate.isRuning) return;
        final int degreesBegin = mRoateAngle;
        mRoateAngle += (clockwise ? 90 : -90);
        mTranslate.withRotate(degreesBegin, mRoateAngle);
        mTranslate.start();

        if (null != mListener) {
            mListener.onRotate(mRoateAngle, 0, 0);
        }
    }

//    public Bitmap getBitmapCache() {
//        final Bitmap bitmap;
//        setDrawingCacheEnabled(true);
//        bitmap = getDrawingCache();
//        setDrawingCacheEnabled(false);
//        return bitmap;
//    }
}