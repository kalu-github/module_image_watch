package com.demo.photo.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.demo.photo.model.ImageModel;
import com.demo.photo.util.DeviceUtil;
import com.demo.photo.util.GlideUtil;
import com.demo.photo.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description: 大图预览, 重构版本
 * create by kalu on 2018/10/11 18:07
 */
public final class PhotoLayout extends FrameLayout implements ViewPager.OnPageChangeListener {

    private final ImageView mImagePlaceholder;
    private PhotoDotView mPhotoPointView;
    private ViewPager viewPager;
    private boolean isPressedBack = false;
    private PhotoDialog mPhotoDialog;

    private PhotoLayout(final Activity activity,
                        final boolean openAnim,
                        final List<PhotoModel> list, final int begin) {
        super(activity.getApplicationContext());
        setBackgroundColor(Color.BLACK);

        final PhotoModel beginModel = list.get(begin);
        final int startX = beginModel.getImageX();
        final int startY = beginModel.getImageY();
        final int startWidth = beginModel.getDrawableIntrinsicWidth();
        final int startHeight = beginModel.getDrawableIntrinsicHeight();

        mImagePlaceholder = new ImageView(getContext().getApplicationContext());
        mImagePlaceholder.setVisibility(openAnim ? View.VISIBLE : View.INVISIBLE);
        addView(mImagePlaceholder);
        LayoutParams params = new LayoutParams(startWidth, startHeight);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = startX;
        params.topMargin = startY;
        // 1.2 位置参数
        mImagePlaceholder.setLayoutParams(params);

        final String urlLittle = list.get(begin).getUrlLittle();
        GlideUtil.loadImageSimple(activity, mImagePlaceholder, urlLittle);

        // 2.初始化ViewPager
        viewPager = new ViewPager(activity.getApplicationContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setVisibility(openAnim ? View.INVISIBLE : View.VISIBLE);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(list.size() + 2);
        addView(viewPager);

        // 3.指示器
        if (list.size() > 1) {
            mPhotoPointView = new PhotoDotView(getContext().getApplicationContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPhotoPointView.setVisibility(openAnim ? View.INVISIBLE : View.VISIBLE);
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.bottomMargin = 100;
            mPhotoPointView.setLayoutParams(layoutParams);
            mPhotoPointView.attach2ViewPage(viewPager, list.size());
            addView(mPhotoPointView);
        }

        // 3.轮播图设置数据
        final PhotoAdapter photoAdapter = new PhotoAdapter(list, begin);
        viewPager.setAdapter(photoAdapter);

        // 3.显示大图
        mPhotoDialog = new PhotoDialog(activity);
        mPhotoDialog.setContentView(PhotoLayout.this);
        mPhotoDialog.show();

        mPhotoDialog.setOnBackPressedListener(() -> {

            if (isPressedBack) return;

            final int position = viewPager.getCurrentItem();
            final PhotoModel photoModel = list.get(position);
            closeAnim(openAnim, photoModel);
        });

        // 4.开始动画
        startAnim(mImagePlaceholder, openAnim, begin, startX, startY, startWidth, startHeight);
    }

    /*******************************************************************************************/

    private final void startAnim(final ImageView animImage, final boolean openAnim, final int begin,
                                 final int startTranX, final int startTranY,
                                 final int startWidth, final int startHeight) {

        final Context context = getContext().getApplicationContext();

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;

        final int scaleX = widthPixels / startWidth;
        final int scaleY = heightPixels / startHeight;
        final int scaleMin = Math.min(scaleX, scaleY);
        LogUtil.e("kalu", "startAnim ==> scaleX = " + scaleX + ", scaleY = " + scaleY + ", scaleMin = " + scaleMin);

        final int endWidth = startWidth * scaleMin;
        final int endHeight = startHeight * scaleMin;
        LogUtil.e("kalu", "startAnim ==> startWidth = " + startWidth + ", endWidth = " + endWidth + ", startHeight = " + startHeight + ", endHeight = " + endHeight);

        final int endTranX = (widthPixels - startWidth) / 2;
        final int endTranY = (heightPixels - startHeight) / 2;

        final float endScaleX = widthPixels * 1.f / startWidth;
        final float endScaleY = heightPixels * 1.f / startHeight;

        // x 方向放大
        final ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(animImage, "scaleX", 1, endScaleX);
        // y 方向放大
        final ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(animImage, "scaleY", 1, endScaleY);
        // x 方向平移
        final ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(animImage, "x", startTranX, endTranX);
        // y 方向平移
        final ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(animImage, "y", startTranY, endTranY);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnima, scaleYAnima, tranXAnima, tranYAnima);
        animatorSet.setDuration(openAnim ? 300 : 0);
        animatorSet.setStartDelay(50);

        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                // 1.删除模拟过渡动画
                if (null != mImagePlaceholder) {
                    mImagePlaceholder.setVisibility(View.INVISIBLE);
                }

                // 2.显示轮播图
                viewPager.setVisibility(View.VISIBLE);
                if (null != mPhotoPointView) {
                    mPhotoPointView.setVisibility(View.VISIBLE);
                }

                // 图片默认索引
                viewPager.setCurrentItem(begin, false);

                if (begin == 0) {
                    onPageSelected(begin);
                }
            }
        });
        animatorSet.start();
    }

    // 结束动画
    private final void closeAnim(final boolean openAnim, PhotoModel photoModel) {

        isPressedBack = true;
        setBackgroundColor(Color.TRANSPARENT);

        if (null != viewPager) {
            viewPager.setEnabled(false);
            viewPager.setVisibility(View.INVISIBLE);
        }

        if (null != mPhotoPointView) {
            mPhotoPointView.setVisibility(View.INVISIBLE);
        }

        if (null != mImagePlaceholder) {
            final String imageUrl = photoModel.getUrl();
            GlideUtil.loadImageSimple(getContext(), mImagePlaceholder, imageUrl);
            mImagePlaceholder.setVisibility(View.VISIBLE);
        }

        final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;

        final int endX = photoModel.getImageX();
        final int beginX = (widthPixels - photoModel.getDrawableIntrinsicWidth()) / 2;

        final int endY = photoModel.getImageY();
        final int beginY = (heightPixels - photoModel.getDrawableIntrinsicHeight()) / 2;

        final float beginScaleX = widthPixels * 1.f / photoModel.getDrawableIntrinsicWidth();
        final float beginScaleY = widthPixels * 1.f / photoModel.getDrawableIntrinsicHeight();

        // x 方向缩小
        final ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "scaleX", beginScaleX, 1);
        // y 方向缩小
        final ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "scaleY", beginScaleY, 1);
        // x 方向平移
        final ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "x", beginX, endX);
        // y 方向平移
        final ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "y", beginY, endY);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(openAnim ? 300 : 0);
        animatorSet.setStartDelay(openAnim ? 50 : 0);
        animatorSet.playTogether(scaleXAnima, scaleYAnima, tranXAnima, tranYAnima);

        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                destoryImageData();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                destoryImageData();
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
                destoryImageData();
            }
        });

        animatorSet.start();
    }

    /**********************************************************************************************/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * 显示大图
     *
     * @param position
     */
    @Override
    public void onPageSelected(final int position) {
        LogUtil.e("kalu", "onPageSelected");

        final ImageView image = (ImageView) viewPager.getChildAt(position);
        final String url = (String) image.getTag(image.getId());
        GlideUtil.loadImagePhoto(getContext(), image, url);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 销毁释放资源文件
     */
    private void destoryImageData() {

        // 移除监听
        if (null != viewPager) {
            viewPager.removeOnPageChangeListener(PhotoLayout.this);
        }

        if (null != mPhotoDialog) {
            mPhotoDialog.cancel();
            mPhotoDialog.dismiss();
        }
    }

    protected void onDrag(float deltaX, float deltaY) {

        if (null == mPhotoDialog) return;

        setBackgroundColor(Color.TRANSPARENT);
    }

    /**********************************************************************************************/

    public static class Builder {

        private OnPhotoChangeListener listener;

        private int imageDefaultPosition = 0;

        private boolean isOpenImageTransAnim = true;
        private int imageBackgroundColor = Color.BLACK;

        private List<PhotoModel> list;

        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setPhotoOpenTransAnim(boolean isOpenImageTransAnim) {
            this.isOpenImageTransAnim = isOpenImageTransAnim;
            return this;
        }

        // 默认显示第几张图片
        public Builder setPhotoDefaultPosition(int imageDefaultPosition) {
            this.imageDefaultPosition = imageDefaultPosition;
            return this;
        }

        public Builder setPhotoBackgroundColor(int imageBackgroundColor) {
            this.imageBackgroundColor = imageBackgroundColor;
            return this;
        }

        public Builder setPhotoModelList(List<PhotoModel> list) {
            this.list = list;
            return this;
        }

        public Builder setPhotoModelList(PhotoModel... photoModels) {
            list = Arrays.asList(photoModels);
            return this;
        }

        public Builder setOnPhotoChangeListener(OnPhotoChangeListener listener) {
            this.listener = listener;
            return this;
        }

        public PhotoLayout show() {

            return new PhotoLayout(activity, true, list, imageDefaultPosition);
        }
    }

    public final static PhotoModel createModel(final ImageView image, final String url, final String littleUrl, @DrawableRes final int defaultId) {

        final int imageWidth = image.getMeasuredWidth();
        final int imageHeight = image.getMeasuredHeight();

        Drawable imageDrawable = image.getDrawable();
        if (null == imageDrawable) {
            image.setImageResource(defaultId);
            imageDrawable = image.getDrawable();
        }

        // 获取图片在屏幕上的可视高度宽度
        final int minimumWidth = imageDrawable.getMinimumWidth();
        final int minimumHeight = imageDrawable.getMinimumHeight();

        final int imageDrawableIntrinsicWidth = imageDrawable.getIntrinsicWidth();
        final int imageDrawableIntrinsicHeight = imageDrawable.getIntrinsicHeight();

        final int[] local = new int[2];
        image.getLocationInWindow(local);
//        final int imageX = local[0] + (imageWidth - imageDrawableIntrinsicWidth) / 2;
//        final int imageY = local[1] - DeviceUtil.getStatusHeight() + (imageHeight - imageDrawableIntrinsicHeight) / 2;

        final ImageModel imageModel = new ImageModel(url, littleUrl, local[0], local[1], imageWidth, imageHeight, minimumWidth, minimumHeight, imageDrawableIntrinsicHeight, imageDrawableIntrinsicWidth);
        LogUtil.e("kalu", "createModel ==> " + imageModel.toString());
        return imageModel;
    }
}
