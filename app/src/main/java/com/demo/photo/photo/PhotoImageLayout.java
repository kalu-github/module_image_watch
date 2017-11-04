package com.demo.photo.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.demo.photo.APP;
import com.demo.photo.dialog.PhotoDialog;
import com.demo.photo.glide.listener.OnGlideLoadChangeListener;
import com.demo.photo.util.DeviceUtil;
import com.demo.photo.util.GlideUtil;

import java.util.Arrays;
import java.util.List;

/**
 * description 大图加载
 * created by kalu on 2016/10/23 13:11
 */
public class PhotoImageLayout extends FrameLayout implements ViewPager.OnPageChangeListener {

    private PhotoImageLayoutAttr attr;

    private PhotoPointerView mPhotoPointerView;
    private ViewPager viewPager;
    private boolean isPressedBack = false;

    private PhotoDialog mPhotoDialog;
    private Activity mActivity;

    private PhotoImageLayout(Activity activity, final PhotoImageLayoutAttr attr) {
        super(activity.getApplicationContext());

        setBackgroundColor(attr.getImageBackgroundColor());
        this.attr = attr;
        this.mActivity = activity;

        // 1.3 创建对象
        int imageDrawableIntrinsicWidth = attr.getDefaultImageDrawableIntrinsicWidth();
        int imageDrawableIntrinsicHeight = attr.getDefaultImageDrawableIntrinsicHeight();
        int imageX = attr.getDefaultImageX();
        int imageY = attr.getDefaultImageY();

        boolean openImageTransAnim = attr.isOpenImageTransAnim();
        ImageView tempImage = new ImageView(getContext());

        tempImage.setVisibility(openImageTransAnim ? View.VISIBLE : View.INVISIBLE);
        addView(tempImage);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageDrawableIntrinsicWidth, imageDrawableIntrinsicHeight);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = imageX;
        params.topMargin = imageY;
        // 1.2 位置参数
        tempImage.setLayoutParams(params);
        GlideUtil.loadImageOriginal(activity, tempImage, attr.getImageOriginalUrl(), null);

        Log.e("ka22lu--11", "imageUrl = " + attr.getImageOriginalUrl());

        Log.e("ka22lu--11", "imageWidth = " + imageDrawableIntrinsicWidth + ", imageX = " + imageX);
        Log.e("ka22lu--11", "imageHeight = " + imageDrawableIntrinsicHeight + ", imageY = " + imageY);

        int imageWidth = tempImage.getWidth();
        int imageHeight = tempImage.getHeight();

        Log.e("ka22lu--22", "imageWidth = " + imageWidth + ", imageX = " + tempImage.getX());
        Log.e("ka22lu--22", "imageHeight = " + imageHeight + ", imageY = " + tempImage.getY());

        // 2.初始化ViewPager
        viewPager = new ViewPager(activity.getApplicationContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(attr.getImageCount() + 2);
        addView(viewPager);

        // 3.指示器
        if (attr.getImageCount() > 1) {
            mPhotoPointerView = new PhotoPointerView(APP.getInstance().getApplicationContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPhotoPointerView.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.bottomMargin = 100;
            mPhotoPointerView.setLayoutParams(layoutParams);
            mPhotoPointerView.attach2ViewPage(viewPager, attr.getImageCount());
            addView(mPhotoPointerView);
        }

        // 3.显示大图
        mPhotoDialog = new PhotoDialog(activity);
        mPhotoDialog.setContentView(PhotoImageLayout.this);
        mPhotoDialog.show();

        mPhotoDialog.setOnBackPressedListener(new PhotoDialog.OnBackPressedListener() {
            @Override
            public void onBackPressed() {

                if (!isPressedBack) {
                    animDismiss();
                    isPressedBack = true;
                }
            }
        });

        // 4.开始动画
        animStart(tempImage);
    }

    /*******************************************************************************************/

    // 开始动画
    private void animStart(final ImageView animImage) {

        Context context = getContext().getApplicationContext();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;

        int startTranX = attr.getDefaultImageX();
        int endTranX = (widthPixels - attr.getDefaultImageDrawableIntrinsicWidth()) / 2;

        int startTranY = attr.getDefaultImageY();
        int endTranY = (heightPixels - attr.getDefaultImageDrawableIntrinsicHeight()) / 2;

        float endScaleVal = widthPixels * 1.f / attr.getDefaultImageDrawableIntrinsicWidth();

        // x 方向放大
        ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(animImage, "scaleX", animImage.getScaleX(), endScaleVal);
        // y 方向放大
        ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(animImage, "scaleY", animImage.getScaleY(), endScaleVal);
        // x 方向平移
        ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(animImage, "x", startTranX, endTranX);
        // y 方向平移
        ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(animImage, "y", startTranY, endTranY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnima, scaleYAnima, tranXAnima, tranYAnima);
        boolean openImageTransAnim = attr.isOpenImageTransAnim();
        animatorSet.setDuration(openImageTransAnim ? 500 : 0);
        animatorSet.setStartDelay(100);

        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                animImage.setScaleX(1);
                animImage.setScaleY(1);

                // 1.删除模拟过渡动画
                removeView(animImage);
                // 2.显示轮播图
                viewPager.setVisibility(View.VISIBLE);
                if (null != mPhotoPointerView) {
                    mPhotoPointerView.setVisibility(View.VISIBLE);
                }
                // 3.轮播图设置数据
                PhotoImageAdapter photoImageAdapter = new PhotoImageAdapter(mActivity);
                photoImageAdapter.setImageAttr(attr);
                photoImageAdapter.setImageLayout(PhotoImageLayout.this);
                viewPager.setAdapter(photoImageAdapter);

                // 图片默认索引
                int imageDefaultPosition = attr.getImageDefaultPosition();
                viewPager.setCurrentItem(imageDefaultPosition);

                if (imageDefaultPosition == 0) {
                    onPageSelected(imageDefaultPosition);
                }
            }
        });
        animatorSet.start();
    }

    // 结束动画
    void animDismiss() {

        if (null != mPhotoPointerView) {
            mPhotoPointerView.setVisibility(View.INVISIBLE);
        }

        int currentItem = viewPager.getCurrentItem();

        float endScale = attr.getImageDrawableIntrinsicWidth(currentItem) * 1.f / getWidth();
        float endScaleY = (attr.getImageDrawableIntrinsicHeight(currentItem) * 1.f / getHeight());
        float endTranX = (getWidth() - (getWidth() * endScale)) * .5f - attr.getImageX(currentItem);
        float endTranY = (getHeight() - (getHeight() * endScaleY)) * .5f - attr.getImageY(currentItem);

        // x 方向缩小
        ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(PhotoImageLayout.this, "scaleX", getScaleX(), endScale);
        // y 方向缩小
        ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(PhotoImageLayout.this, "scaleY", getScaleY(), endScale);
        // x 方向平移
        ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(PhotoImageLayout.this, "x", getTranslationX(), -endTranX);
        // y 方向平移
        ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(PhotoImageLayout.this, "y", getTranslationY(), -endTranY);

        AnimatorSet animatorSet = new AnimatorSet();
        boolean openImageTransAnim = attr.isOpenImageTransAnim();
        animatorSet.setDuration(openImageTransAnim ? 500 : 0);
        animatorSet.setStartDelay(openImageTransAnim ? 100 : 0);
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

    @Override
    public void onPageSelected(final int position) {

        final PhotoImageView image = (PhotoImageView) viewPager.getChildAt(position);

        Object tag = image.getTag(image.getId());

        final String bigUrl = attr.getImageUrlList().get(position);

        if (null != tag && tag instanceof String && bigUrl.equals(tag)) {
            return;
        }

        // 1. 设置默认图片
        if (TextUtils.isEmpty(bigUrl)) {

            if (attr.getImageDefaultResource() != -1) {
                image.setImageResource(attr.getImageDefaultResource());
            }

        } else {

            image.setTag(image.getId(), bigUrl);
            GlideUtil.loadImageOriginal(mActivity, image, bigUrl, new OnGlideLoadChangeListener() {
                @Override
                public void onLoadChange(String imageUrl, long loadSize, long totalSize, long percent) {

                    image.setLoadProgress(percent);
                }
            });
        }
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
            viewPager.removeOnPageChangeListener(PhotoImageLayout.this);
        }

        if (null != mPhotoDialog) {
            mPhotoDialog.cancel();
            mPhotoDialog.dismiss();
        }
    }

    /**********************************************************************************************/

    public static class Builder {

        private Activity activity;
        private OnImageChangeListener listener;

        private int imageDefaultPosition = 0;

        // 默认占位图
        private int imageDefaultResource = -1;

        private boolean isOpenImageTransAnim = true;
        private int imageBackgroundColor = Color.BLACK;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        private List<String> imageUrlList;
        private List<String> imageLittleUrlList;

        public Builder setImageUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
            return this;
        }

        public Builder setImageUrlList(String... imageUrl) {
            this.imageUrlList = Arrays.asList(imageUrl);
            return this;
        }

        public Builder setImageLittleUrlList(List<String> imageLittleUrlList) {
            this.imageLittleUrlList = imageLittleUrlList;
            return this;
        }

        public Builder setImageLittleUrlList(String... imageLittleUrlList) {
            this.imageLittleUrlList = Arrays.asList(imageLittleUrlList);
            return this;
        }

        private List<ImageView> imageViews;

        public Builder setImageList(List<ImageView> imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        public Builder setImageList(ImageView... views) {
            this.imageViews = Arrays.asList(views);
            return this;
        }

        public Builder setImageOpenTransAnim(boolean isOpenImageTransAnim) {
            this.isOpenImageTransAnim = isOpenImageTransAnim;
            return this;
        }

        // 默认显示第几张图片
        public Builder setImageDefaultPosition(int imageDefaultPosition) {
            this.imageDefaultPosition = imageDefaultPosition;
            return this;
        }

        public Builder setImageBackgroundColor(int imageBackgroundColor) {
            this.imageBackgroundColor = imageBackgroundColor;
            return this;
        }

        public Builder setImageDefaultResource(int imageDefaultResource) {
            this.imageDefaultResource = imageDefaultResource;
            return this;
        }

        public Builder setOnImageClickChangeListener(OnImageChangeListener listener) {
            this.listener = listener;
            return this;
        }

        private boolean imageLongPressSave;

        /**
         * 长按保存
         *
         * @param imageLongPressSave 长按保存
         * @return
         */
        public Builder setImageLongPressSave(boolean imageLongPressSave) {
            this.imageLongPressSave = imageLongPressSave;
            return this;
        }

        public PhotoImageLayout show() {

            PhotoImageLayoutAttr attr = new PhotoImageLayoutAttr();

            if (imageUrlList != null && !imageUrlList.isEmpty()) {
                attr.setImageUrlList(imageUrlList);
            }

            if (imageLittleUrlList != null && !imageLittleUrlList.isEmpty()) {
                attr.setImageLittleUrlList(imageLittleUrlList);
            }

            if (null != listener) {
                attr.setOnImageChangeListener(listener);
            }

            attr.setOpenImageTransAnim(isOpenImageTransAnim);
            // 背景颜色
            attr.setImageBackgroundColor(imageBackgroundColor);
            // 默认显示图片位置
            attr.setImageDefaultPosition(imageDefaultPosition);
            // 默认图片占位图
            attr.setImageDefaultResource(imageDefaultResource);
            // 长按保存图片
            attr.setImageLongPressSave(imageLongPressSave);

            for (int i = 0; i < imageViews.size(); i++) {

                ImageView imageOriginal = imageViews.get(i);

                int imageWidth = imageOriginal.getWidth();
                int imageHeight = imageOriginal.getHeight();

                Drawable imageDrawable = imageOriginal.getDrawable();
                int imageDrawableIntrinsicWidth = imageDrawable.getIntrinsicWidth();
                int imageDrawableIntrinsicHeight = imageDrawable.getIntrinsicHeight();

                final int[] local = new int[2];
                imageOriginal.getLocationOnScreen(local);
                int imageX = local[0] + (imageWidth - imageDrawableIntrinsicWidth) / 2;
                int imageY = local[1] - DeviceUtil.getStatusHeight() + (imageHeight - imageDrawableIntrinsicHeight) / 2;

                PhotoImageLayoutAttr.PhotoImageInfo photoImageInfo = new PhotoImageLayoutAttr.PhotoImageInfo();
                photoImageInfo.setImageDrawableIntrinsicWidth(imageDrawableIntrinsicWidth);
                photoImageInfo.setImageDrawableIntrinsicHeight(imageDrawableIntrinsicHeight);
                photoImageInfo.setImageX(imageX);
                photoImageInfo.setImageY(imageY);
                attr.addImageLocalInfo(photoImageInfo);
            }

            return new PhotoImageLayout(activity, attr);
        }
    }
}
