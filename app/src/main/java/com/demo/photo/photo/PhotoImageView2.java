package com.demo.photo.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.demo.photo.util.GlideUtil;

/**
 * description: 可缩放、旋转、平移的ImageView
 * created by kalu on 17-10-15 上午5:27
 */
final class PhotoImageView2 extends AppCompatImageView {

    private OnPhotoChangeListener mListener = null;

    public void setOnImageChangeListener(OnPhotoChangeListener listener) {
        mListener = listener;
    }

    // 长按保存图片
    private boolean imaageLongPressSave = false;

    public void setImaageLongPressSave(boolean imaageLongPressSave) {
        this.imaageLongPressSave = imaageLongPressSave;
    }

    /**
     * 自动缩放、旋转、平移动画的持续时间
     */
    private static final float DEFAULT_ANIM_TIME = 200.0f;
    /**
     * 最大、最小缩放值
     */
    private static final float DEFAULT_MIN_SCALE = 0.75f;
    private static final float DEFAULT_MAX_SCALE = 5.0f;

    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    /**
     * 标准化的缩放倍数
     */
    private float mRealScale = 1;
    /**
     * 适应屏幕时，图片的宽高
     */
    private float mMatchedImageWidth = 0, mMatchedImageHeight = 0;

    private enum State {
        NONE, ONE_POINT, TWO_POINT,
    }

    private State mState = State.NONE;
    private boolean mIsTranslate = false;
    private boolean mIsRotate = false;
    private boolean mIsScale = false;

    private boolean mIsRotateEnabled = true;
    /**
     * 需要自动旋转的角度
     */
    private float remainDegree = 0f;

    private int mViewHeight, mViewWidth;

    private final Matrix mMatrix = new Matrix();

    private Fling mFling = null;
    private Rotate mRotate = null;
    private Translate mTranslate = null;
    private Scale mScale = null;

    /**
     * 图片旋转的角度，顺时针方向为正
     */
    private float mDegree = 0;

    private boolean mReachLeft = false;
    private boolean mReachRight = false;

    private String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private PhotoLayout mPhotoLayout;

    public void setPhotoImageLayout(PhotoLayout mPhotoLayout) {
        this.mPhotoLayout = mPhotoLayout;
    }

    /**********************************************************************************************/

    public PhotoImageView2(Context context) {
        this(context, null, 0);
    }

    public PhotoImageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(mOnTouchListener);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        formatImageScaleType();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        formatImageScaleType();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        formatImageScaleType();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        formatImageScaleType();
    }

    private void formatImageScaleType() {

        if (null == mMatrix) return;

        Drawable drawable = getDrawable();
        if (null == drawable) return;

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        final float temp1 = (float) mViewWidth / drawableWidth;
        final float temp2 = (float) mViewHeight / drawableHeight;
        final float scale = (drawableWidth > drawableHeight) ? Math.min(temp1, temp2) : temp1;

        mMatchedImageWidth = drawableWidth * scale;
        mMatchedImageHeight = drawableHeight * scale;

        // 缩放
        mMatrix.setScale(scale, scale);
        // 平移, 左上角位置
        final float dx = (mViewWidth - mMatchedImageWidth) / 2;
        final float dy = (mViewHeight - mMatchedImageHeight) / 2;
        mMatrix.postTranslate(dx, dy);
        // 旋转
        mMatrix.postRotate(mDegree, mViewWidth / 2, mViewHeight / 2);

        setImageMatrix(mMatrix);
    }

    /**********************************************************************************************/

    public void setMaxScale(float scale) {
        mMaxScale = scale;
    }

    /**
     * 获取当前缩放值
     *
     * @return
     */
    public float getCurrentScale() {
        return mRealScale;
    }

    /**
     * 关闭旋转功能
     */
    public void disableRotate() {
        mIsRotateEnabled = false;
    }

    private void fixTranslation() {
        fixTranslation(0, 0);
    }

    /**
     * 移动图片，使适应屏幕
     *
     * @param deltaX
     * @param deltaY
     */
    private void fixTranslation(float deltaX, float deltaY) {

        float[] matrixValues = new float[9];
        mMatrix.getValues(matrixValues);

        float transX = matrixValues[Matrix.MTRANS_X] + deltaX;
        float transY = matrixValues[Matrix.MTRANS_Y] + deltaY;

        final boolean result = mDegree == 0 || mDegree == 180;
        final float width = result ? mMatchedImageWidth : mMatchedImageHeight * mRealScale;
        final float height = result ? mMatchedImageHeight : mMatchedImageWidth * mRealScale;
        final Size size = new Size(width, height);

        transX = getFixTrans(transX, size.getWidth(), mViewWidth, true);
        transY = getFixTrans(transY, size.getHeight(), mViewHeight, false);
        matrixValues[Matrix.MTRANS_X] = transX;
        matrixValues[Matrix.MTRANS_Y] = transY;
        mMatrix.setValues(matrixValues);
        setImageMatrix(mMatrix);
    }

    private float getFixTrans(float transSize, float contentSize,
                              float viewSize, boolean isWidth) {
        return getFixTrans(transSize, contentSize, viewSize, isWidth, true);
    }

    /**
     * 得到合适的偏移量
     *
     * @param transSize
     * @param contentSize
     * @param viewSize
     * @param isWidth
     * @param fixed
     * @return
     */
    private float getFixTrans(float transSize, float contentSize,
                              float viewSize, boolean isWidth, boolean fixed) {
        int degree = 90;
        if (!isWidth) {
            degree = 270;
        }
        if (viewSize >= contentSize) {
            transSize = (viewSize - contentSize) / 2;
            if (mDegree == 180 || mDegree == degree) {
                transSize += contentSize;
            }
        } else {
            float minSize, maxSize;
            minSize = viewSize - contentSize;
            maxSize = 0;
            if (mDegree == 180 || mDegree == degree) {
                minSize += contentSize;
                maxSize += contentSize;
            }
            if (isWidth) {
                if (transSize >= maxSize) {
                    mReachRight = true;
                    mReachLeft = false;
                } else if (transSize <= minSize) {
                    mReachLeft = true;
                    mReachRight = false;
                } else {
                    mReachRight = false;
                    mReachLeft = false;
                }
            }
            if (fixed) {
                transSize = transSize > maxSize ? maxSize : transSize;
                transSize = transSize < minSize ? minSize : transSize;
            }
        }
        return transSize;
    }

    /**
     * 得到合适的旋转角度
     *
     * @param degree
     * @return
     */
    private float ModifyDegree(float degree) {
        while (degree < 0) {
            degree += 360;
        }
        degree %= 360;
        if (degree <= 45 || degree > 315) {
        } else if (degree > 45 && degree <= 135) {
            mDegree += 90;
        } else if (degree > 135 && degree <= 225) {
            mDegree += 180;
        } else {
            mDegree += 270;
        }
        mDegree %= 360;
        if (mListener != null) {
            mListener.onRotate(mDegree);
        }
        degree %= 90;
        if (degree > 45) {
            degree = 90 - degree;
        } else {
            degree = -degree;
        }
        return degree;
    }

    private float rangeMoveX, rangeMoveY;

    /**
     * 缩放图片
     *
     * @param deltaScale
     * @param focusX
     * @param focusY
     */
    private void scaleImage(float deltaScale, float focusX, float focusY) {

        float lowerScale, upperScale;
        lowerScale = mMinScale;
        upperScale = mMaxScale;

        float origScale = mRealScale;
        mRealScale *= deltaScale;
        if (mRealScale > upperScale) {
            mRealScale = upperScale;
            deltaScale = upperScale / origScale;
        } else if (mRealScale < lowerScale) {
            mRealScale = lowerScale;
            deltaScale = lowerScale / origScale;
        }

        if (mListener != null) {
            mListener.onScale(mRealScale);
        }

        mMatrix.postScale(deltaScale, deltaScale, focusX, focusY);
        setImageMatrix(mMatrix);
    }

    private class Scale implements Runnable {
        private long startTime;
        private float startScale, targetScale;
        private float bitmapX, bitmapY;
        private Interpolator interpolator = new DecelerateInterpolator();
        private PointF startTouch;
        private PointF endTouch;
        private float duration = DEFAULT_ANIM_TIME;
        private boolean isScale = false;
        private boolean fixTrans = false;

        public Scale(float targetScale, float focusX, float focusY,
                     boolean fixTrans) {
            startScale = mRealScale;
            this.targetScale = targetScale;
            PointF bitmapPoint = transformCoordTouchToBitmap(focusX, focusY,
                    false);
            bitmapX = bitmapPoint.x;
            bitmapY = bitmapPoint.y;

            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY);
            endTouch = new PointF(mViewWidth / 2, mViewHeight / 2);
            this.fixTrans = fixTrans;
        }

        public void start() {
            startTime = System.currentTimeMillis();
            mIsScale = true;
            isScale = true;
            postRunnable(this);
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }

        @Override
        public void run() {
            float t = interpolate();
            float deltaScale = calculateDeltaScale(t);
            scaleImage(deltaScale, bitmapX, bitmapY);
            translateImageToCenterTouchPosition(t);
            if (fixTrans) {
                fixTranslation();
            } else {
                setImageMatrix(mMatrix);
            }
            if (mIsScale) {
                if (!isScale) {
                    mIsScale = false;
                }
                postRunnable(this);
            } else {
                if (!fixTrans) {
                    if (!mIsTranslate) {
                        mTranslate = new Translate();
                        mTranslate.start();
                    }
                }
            }
        }

        private PointF transformCoordTouchToBitmap(float x, float y,
                                                   boolean clipToBitmap) {
            float[] m = new float[9];
            mMatrix.getValues(m);

            float origW = getDrawable().getIntrinsicWidth();
            float origH = getDrawable().getIntrinsicHeight();
            float transX = m[Matrix.MTRANS_X];
            float transY = m[Matrix.MTRANS_Y];

            final boolean result = mDegree == 0 || mDegree == 180;
            final float width = result ? mMatchedImageWidth : mMatchedImageHeight * mRealScale;
            final float height = result ? mMatchedImageHeight : mMatchedImageWidth * mRealScale;
            final Size size = new Size(width, height);

            float finalX = ((x - transX) * origW) / size.getWidth();
            float finalY = ((y - transY) * origH) / size.getHeight();
            if (clipToBitmap) {
                finalX = Math.min(Math.max(finalX, 0), origW);
                finalY = Math.min(Math.max(finalY, 0), origH);
            }
            return new PointF(finalX, finalY);
        }

        private PointF transformCoordBitmapToTouch(float bx, float by) {
            float[] m = new float[9];
            mMatrix.getValues(m);

            float origW = getDrawable().getIntrinsicWidth();
            float origH = getDrawable().getIntrinsicHeight();
            float px = bx / origW;
            float py = by / origH;

            final boolean result = mDegree == 0 || mDegree == 180;
            final float width = result ? mMatchedImageWidth : mMatchedImageHeight * mRealScale;
            final float height = result ? mMatchedImageHeight : mMatchedImageWidth * mRealScale;
            final Size size = new Size(width, height);

            float finalX = m[Matrix.MTRANS_X] + size.getWidth() * px;
            float finalY = m[Matrix.MTRANS_Y] + size.getHeight() * py;
            return new PointF(finalX, finalY);
        }

        private void translateImageToCenterTouchPosition(float t) {
            float targetX = startTouch.x + t * (endTouch.x - startTouch.x);
            float targetY = startTouch.y + t * (endTouch.y - startTouch.y);
            PointF curr = transformCoordBitmapToTouch(bitmapX, bitmapY);
            mMatrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        private float interpolate() {
            long currTime = System.currentTimeMillis();
            float elapsed = (currTime - startTime) / duration;
            elapsed = Math.min(1f, elapsed);
            if (elapsed >= 1) {
                isScale = false;
            }
            return interpolator.getInterpolation(elapsed);
        }

        private float calculateDeltaScale(float t) {
            float zoom = startScale + t * (targetScale - startScale);
            return zoom / mRealScale;
        }
    }

    private class Translate implements Runnable {
        private float beginTransX, beginTransY;
        private float endTransX, endTransY;
        private float curTransX, curTransY;
        private Interpolator interpolator = new DecelerateInterpolator();
        private long startTime;
        private Size size;
        private float duration = DEFAULT_ANIM_TIME;
        private boolean isTranslate = false;

        public Translate() {
            this(mRealScale);
        }

        public Translate(float scaleFactor) {

            final boolean result = mDegree == 0 || mDegree == 180;
            final float width = result ? mMatchedImageWidth : mMatchedImageHeight * scaleFactor;
            final float height = result ? mMatchedImageHeight : mMatchedImageWidth * scaleFactor;
            size = new Size(width, height);
            updateBeginAndEnd();
        }

        public void updateBeginAndEnd() {
            float[] matrixValues = new float[9];
            mMatrix.getValues(matrixValues);

            beginTransX = matrixValues[Matrix.MTRANS_X];
            beginTransY = matrixValues[Matrix.MTRANS_Y];
            endTransX = getFixTrans(beginTransX, size.getWidth(), mViewWidth,
                    true);
            endTransY = getFixTrans(beginTransY, size.getHeight(), mViewHeight,
                    false);
        }

        public void start() {
            if (beginTransX == endTransX && beginTransY == endTransY) {
                return;
            }
            mIsTranslate = true;
            isTranslate = true;
            startTime = System.currentTimeMillis();
            postRunnable(this);
        }

        private void interpolateTrans() {
            float elapsed = (System.currentTimeMillis() - startTime) / duration;
            elapsed = Math.min(1f, elapsed);
            if (elapsed >= 1) {
                isTranslate = false;
            }
            float percent = interpolator.getInterpolation(elapsed);
            curTransX = beginTransX + (endTransX - beginTransX) * percent;
            curTransY = beginTransY + (endTransY - beginTransY) * percent;
        }

        @Override
        public void run() {
            interpolateTrans();

            float[] matrixValues = new float[9];
            mMatrix.getValues(matrixValues);

            matrixValues[Matrix.MTRANS_X] = curTransX;
            matrixValues[Matrix.MTRANS_Y] = curTransY;
            mMatrix.setValues(matrixValues);
            setImageMatrix(mMatrix);
            if (mIsTranslate) {
                if (!isTranslate) {
                    mIsTranslate = false;
                }
                postRunnable(this);
            }
        }
    }

    private class Rotate implements Runnable {
        private float totalDegree;
        private float lastDegree = 0;
        private Interpolator interpolator = new DecelerateInterpolator();
        private long startTime;
        private float focusX, focusY;
        private boolean isRotate = false;

        public Rotate(float d) {
            this(d, mViewWidth / 2, mViewHeight / 2);
        }

        public Rotate(float d, float focusX, float focusY) {
            totalDegree = d;
            this.focusX = focusX;
            this.focusY = focusY;
        }

        public void start() {
            mIsRotate = true;
            isRotate = true;
            startTime = System.currentTimeMillis();
            postRunnable(this);
        }

        private float interpolateDegree() {
            float elapsed = (System.currentTimeMillis() - startTime)
                    / DEFAULT_ANIM_TIME;
            elapsed = Math.min(1f, elapsed);
            if (elapsed >= 1) {
                isRotate = false;
            }
            float curDegree = totalDegree
                    * interpolator.getInterpolation(elapsed);
            float deltaDegree = curDegree - lastDegree;
            lastDegree = curDegree;
            return deltaDegree;
        }

        @Override
        public void run() {
            float deltaDegree = interpolateDegree();
            mMatrix.postRotate(deltaDegree, focusX, focusY);
            setImageMatrix(mMatrix);
            if (mIsRotate) {
                if (!isRotate) {
                    mIsRotate = false;
                }
                postRunnable(this);
            } else {
                if (mRealScale < 1 && !mIsScale) {
                    mScale = new Scale(1, mViewWidth / 2, mViewHeight / 2,
                            false);
                    mScale.start();
                    return;
                }
                if (!mIsTranslate) {
                    mTranslate = new Translate();
                    mTranslate.start();
                }
            }
        }
    }

    private class Fling implements Runnable {
        private Scroller scroller = null;
        private int lastX, lastY;

        public Fling(int velocityX, int velocityY) {
            scroller = new Scroller(getContext());

            float[] mtrixValues = new float[9];
            mMatrix.getValues(mtrixValues);

            final boolean result = mDegree == 0 || mDegree == 180;
            final float width = result ? mMatchedImageWidth : mMatchedImageHeight * mRealScale;
            final float height = result ? mMatchedImageHeight : mMatchedImageWidth * mRealScale;
            final Size size = new Size(width, height);

            int startX = (int) mtrixValues[Matrix.MTRANS_X];
            int startY = (int) mtrixValues[Matrix.MTRANS_Y];
            int minX, maxX, minY, maxY;

            if (size.getWidth() > mViewWidth) {
                minX = mViewWidth - (int) size.getWidth();
                maxX = 0;
                if (mDegree == 90 || mDegree == 180) {
                    minX += size.getWidth();
                    maxX += size.getWidth();
                }
            } else {
                minX = maxX = startX;
            }

            if (size.getHeight() > mViewHeight) {
                minY = mViewHeight - (int) size.getHeight();
                maxY = 0;
                if (mDegree == 270 || mDegree == 180) {
                    minY += size.getHeight();
                    maxY += size.getHeight();
                }
            } else {
                minY = maxY = startY;
            }

            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            lastX = startX;
            lastY = startY;
        }

        public void start() {
            postRunnable(this);
        }

        public void cancelFling() {
            if (scroller != null) {
                scroller.forceFinished(true);
            }
        }

        @Override
        public void run() {
            if (scroller.isFinished()) {
                scroller = null;
                return;
            }

            if (scroller.computeScrollOffset()) {
                int newX = scroller.getCurrX();
                int newY = scroller.getCurrY();
                int deltaX = newX - lastX;
                int deltaY = newY - lastY;
                lastX = newX;
                lastY = newY;
                if (mListener != null) {
                    mListener.onFling(deltaX, deltaY);
                }
                fixTranslation(deltaX, deltaY);
                postRunnable(this);
            } else {
                scroller = null;
            }
        }
    }

    private void postRunnable(Runnable runnable) {
        postDelayed(runnable, 1000 / 60);
    }

    private class Size {
        private float mWidth;
        private float mHeight;

        public Size(float width, float height) {
            mWidth = width;
            mHeight = height;
        }

        public float getWidth() {
            return mWidth;
        }

        public float getHeight() {
            return mHeight;
        }
    }

    /**********************************************************************************************/

    private final ScaleGestureDetector mSimpleOnScaleGestureListener = new ScaleGestureDetector(getContext().getApplicationContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }
    });

    private final GestureDetector mSimpleOnGestureListener = new GestureDetector(getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (null != mPhotoLayout) {
                rangeMoveX += Math.abs(distanceX);
                rangeMoveY += Math.abs(distanceY);
                mPhotoLayout.onDrag(rangeMoveX, rangeMoveY);
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent e) {

            if (!imaageLongPressSave) return;
            GlideUtil.saveImageToGallery(imageUrl);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (mRealScale != 1) {
                onDoubleTap(e);
            } else {
                if (null != mPhotoLayout) {
                    mPhotoLayout.animDismiss();
                }
            }

            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击图片缩放的最大或最小
            if (mState == State.NONE && !mIsTranslate && !mIsRotate && !mIsScale) {
                float targetZoom = (mRealScale == 1) ? mMaxScale : 1;
                mScale = new Scale(targetZoom, e.getX(), e.getY(), true);
                mScale.setDuration(500);
                mScale.start();
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mFling != null) {
                mFling.cancelFling();
            }
            mFling = new Fling((int) velocityX, (int) velocityY);
            mFling.start();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    });

    private final OnTouchListener mOnTouchListener = new OnTouchListener() {
        private PointF lastP1 = new PointF();
        private PointF lastP2 = new PointF();
        private PointF curP1 = new PointF();
        private PointF curP2 = new PointF();

        private float deltaX, deltaY;
        private float totalDeltaX = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mIsTranslate && !mIsRotate && !mIsScale) {
                if (mRealScale == 1) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                mSimpleOnGestureListener.onTouchEvent(event);
                mSimpleOnScaleGestureListener.onTouchEvent(event);
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mFling != null) {
                            mFling.cancelFling();
                        }
                        mState = State.ONE_POINT;
                        lastP1.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        switch (mState) {
                            case ONE_POINT:
                                // 拖动
                                performDrag(event);
                                break;
                            case TWO_POINT:
                                // 旋转
                                if (mIsRotateEnabled) {
                                    performRotate(event);
                                }
                                break;
                            default:
                                break;
                        }
                        if (((mReachRight && deltaX > 0) || (mReachLeft && deltaX < 0))
                                && Math.abs(totalDeltaX) < 100) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mState = State.NONE;
                        totalDeltaX = 0;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (mState == State.ONE_POINT) {
                            mState = State.TWO_POINT;
                            lastP2.set(event.getX(1), event.getY(1));
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mState = State.NONE;
                        if (!mIsRotate) {
                            // 自动旋转图片
                            remainDegree = ModifyDegree(remainDegree);
                            mRotate = new Rotate(remainDegree);
                            mRotate.start();
                            remainDegree = 0;
                        }
                        break;
                }
                setImageMatrix(mMatrix);
            }
            return true;
        }

        /**
         * 拖动图片
         *
         * @param event
         */
        private void performDrag(MotionEvent event) {
            curP1.set(event.getX(0), event.getY(0));
            deltaX = curP1.x - lastP1.x;
            deltaY = curP1.y - lastP1.y;
            totalDeltaX += Math.abs(deltaX);
            fixTranslation(deltaX, deltaY);
            lastP1.set(curP1);
            if (mListener != null) {
                mListener.onDrag(deltaX, deltaY);
            }
        }

        /**
         * 旋转图片
         *
         * @param event
         */
        private void performRotate(MotionEvent event) {
            curP1.set(event.getX(0), event.getY(0));
            curP2.set(event.getX(1), event.getY(1));
            float curDegree = calculateDegree(lastP1, lastP2, curP1, curP2);
            lastP1.set(curP1);
            lastP2.set(curP2);
            mMatrix.postRotate(curDegree, mViewWidth / 2, mViewHeight / 2);
            remainDegree += curDegree;
        }

        /**
         * 根据两对坐标点计算旋转角度
         *
         * @param a1
         * @param b1
         * @param a2
         * @param b2
         * @return
         */
        private float calculateDegree(PointF a1, PointF b1, PointF a2, PointF b2) {
            float degree = 0;
            double d1, d2;
            d1 = Math.atan((b1.y - a1.y) / (b1.x - a1.x));
            d2 = Math.atan((b2.y - a2.y) / (b2.x - a2.x));
            d1 = (float) (d1 * 180 / Math.PI);
            d2 = (float) (d2 * 180 / Math.PI);
            degree = (float) (d2 - d1);
            if (degree > 90) {
                degree = 180 - degree;
            } else if (degree < -90) {
                degree += 180;
            }
            return degree;
        }
    };
}
