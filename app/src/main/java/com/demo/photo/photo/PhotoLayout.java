package com.demo.photo.photo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
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
public final class PhotoLayout extends FrameLayout implements ViewPager.OnPageChangeListener {

    private PhotoLayoutAttr attr;

    private PhotoPointView mPhotoPointView;
    private ViewPager viewPager;
    private boolean isPressedBack = false;

    private PhotoDialog mPhotoDialog;
    private Activity mActivity;

    private final PhotoLoadView load;
    private final ImageView mImagePlaceholder;

    private Activity activity;

    private PhotoLayout(Activity activity, final PhotoLayoutAttr attr) {
        super(activity.getApplicationContext());
        this.activity = activity;
        //  setBackgroundColor(Color.GREEN);

        setBackgroundColor(attr.getImageBackgroundColor());
        this.attr = attr;
        this.mActivity = activity;

        // 加载进度条
        load = new PhotoLoadView(getContext().getApplicationContext());
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(100, 100);
        params1.gravity = Gravity.CENTER;
        load.setLayoutParams(params1);
        addView(load);

        // 1.3 创建对象
        int imageDrawableIntrinsicWidth = attr.getDefaultImageDrawableIntrinsicWidth();
        int imageDrawableIntrinsicHeight = attr.getDefaultImageDrawableIntrinsicHeight();
        int imageX = attr.getDefaultImageX();
        int imageY = attr.getDefaultImageY();

        boolean openImageTransAnim = attr.isOpenImageTransAnim();
        mImagePlaceholder = new ImageView(getContext().getApplicationContext());
        mImagePlaceholder.setVisibility(openImageTransAnim ? View.VISIBLE : View.INVISIBLE);
        addView(mImagePlaceholder);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageDrawableIntrinsicWidth, imageDrawableIntrinsicHeight);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = imageX;
        params.topMargin = imageY;
        // 1.2 位置参数
        mImagePlaceholder.setLayoutParams(params);
        GlideUtil.loadImageSimple(activity, mImagePlaceholder, attr.getImageOriginalUrl(), null);

        // 2.初始化ViewPager
        viewPager = new ViewPager(activity.getApplicationContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(attr.getImageCount() + 2);
        addView(viewPager);

        // 3.指示器
        if (attr.getImageCount() > 1) {
            mPhotoPointView = new PhotoPointView(APP.getInstance().getApplicationContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPhotoPointView.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.bottomMargin = 100;
            mPhotoPointView.setLayoutParams(layoutParams);
            mPhotoPointView.attach2ViewPage(viewPager, attr.getImageCount());
            addView(mPhotoPointView);
        }

        // 3.轮播图设置数据
        PhotoImageAdapter photoImageAdapter = new PhotoImageAdapter(mActivity);
        photoImageAdapter.setImageAttr(attr);
        photoImageAdapter.setImageLayout(PhotoLayout.this);
        viewPager.setAdapter(photoImageAdapter);

        // 3.显示大图
        mPhotoDialog = new PhotoDialog(activity);
        mPhotoDialog.setContentView(PhotoLayout.this);
        mPhotoDialog.show();

        mPhotoDialog.setOnBackPressedListener(new PhotoDialog.OnBackPressedListener() {
            @Override
            public void onBackPressed() {

                if (isPressedBack) return;
                setBackgroundColor(Color.TRANSPARENT);
                animDismiss();
                isPressedBack = true;
            }
        });

        // 4.开始动画
        animStart(mImagePlaceholder);
    }

    /*******************************************************************************************/

    // 开始动画
    private void animStart(final ImageView animImage) {

        if (null != viewPager) {
            viewPager.setEnabled(true);
        }

        final Context context = getContext().getApplicationContext();

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;

        final int startTranX = attr.getDefaultImageX();
        final int endTranX = (widthPixels - attr.getDefaultImageDrawableIntrinsicWidth()) / 2;

        final int startTranY = attr.getDefaultImageY();
        final int endTranY = (heightPixels - attr.getDefaultImageDrawableIntrinsicHeight()) / 2;

        final float endScale = widthPixels * 1.f / attr.getDefaultImageDrawableIntrinsicWidth();

        // x 方向放大
        final ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(animImage, "scaleX", 1, endScale);
        // y 方向放大
        final ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(animImage, "scaleY", 1, endScale);
        // x 方向平移
        final ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(animImage, "x", startTranX, endTranX);
        // y 方向平移
        final ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(animImage, "y", startTranY, endTranY);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnima, scaleYAnima, tranXAnima, tranYAnima);
        final boolean openImageTransAnim = attr.isOpenImageTransAnim();
        animatorSet.setDuration(openImageTransAnim ? 500 : 0);
        animatorSet.setStartDelay(100);

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
                final int imageDefaultPosition = attr.getImageDefaultPosition();
                viewPager.setCurrentItem(imageDefaultPosition, false);

                if (imageDefaultPosition == 0) {
                    onPageSelected(imageDefaultPosition);
                }
            }
        });
        animatorSet.start();
    }

    // 结束动画
    void animDismiss() {

        if (null != viewPager) {
            viewPager.setEnabled(false);
            viewPager.setVisibility(View.INVISIBLE);
        }

        final int position = viewPager.getCurrentItem();
        if (null != mImagePlaceholder) {
            final PhotoImageView image = (PhotoImageView) viewPager.getChildAt(position);
            final String imageUrl = image.getImageUrl();
            GlideUtil.loadImageSimple(activity, mImagePlaceholder, imageUrl, null);
            mImagePlaceholder.setVisibility(View.VISIBLE);
        }

        final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;

        final int endX = attr.getImageX(position);
        final int beginX = (widthPixels - attr.getImageDrawableIntrinsicWidth(position)) / 2;

        final int endY = attr.getImageY(position);
        final int beginY = (heightPixels - attr.getImageDrawableIntrinsicHeight(position)) / 2;

        final float beginScaleX = widthPixels * 1.f / attr.getImageDrawableIntrinsicWidth(position);
        final float beginScaleY = widthPixels * 1.f / attr.getImageDrawableIntrinsicHeight(position);

        // x 方向缩小
        final ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "scaleX", beginScaleX, 1);
        // y 方向缩小
        final ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "scaleY", beginScaleY, 1);
        // x 方向平移
        final ObjectAnimator tranXAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "x", beginX, endX);
        // y 方向平移
        final ObjectAnimator tranYAnima = ObjectAnimator.ofFloat(mImagePlaceholder, "y", beginY, endY);

        final AnimatorSet animatorSet = new AnimatorSet();
        final boolean openImageTransAnim = attr.isOpenImageTransAnim();
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

        final ImageView image = (ImageView) viewPager.getChildAt(position);
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

                    if (null == load) return;
                    load.setLoadProgress(percent);
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

        private Activity activity;
        private OnPhotoChangeListener listener;

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

        public Builder setPhotoUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
            return this;
        }

        public Builder setPhotoUrlList(String... imageUrl) {
            this.imageUrlList = Arrays.asList(imageUrl);
            return this;
        }

        public Builder setPhotoLittleUrlList(List<String> imageLittleUrlList) {
            this.imageLittleUrlList = imageLittleUrlList;
            return this;
        }

        public Builder setPhotoLittleUrlList(String... imageLittleUrlList) {
            this.imageLittleUrlList = Arrays.asList(imageLittleUrlList);
            return this;
        }

        private List<ImageView> imageViews;

        public Builder setPhotoViewList(List<ImageView> imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        public Builder setPhotoViewList(ImageView... views) {
            this.imageViews = Arrays.asList(views);
            return this;
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

        public Builder setPhotoDefaultResource(int imageDefaultResource) {
            this.imageDefaultResource = imageDefaultResource;
            return this;
        }

        public Builder setOnPhotoClickChangeListener(OnPhotoChangeListener listener) {
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
        public Builder setPhotoLongPressSave(boolean imageLongPressSave) {
            this.imageLongPressSave = imageLongPressSave;
            return this;
        }

        public PhotoLayout show() {

            PhotoLayoutAttr attr = new PhotoLayoutAttr();

            if (imageUrlList != null && !imageUrlList.isEmpty()) {
                attr.setImageUrlList(imageUrlList);
            }

            if (imageLittleUrlList != null && !imageLittleUrlList.isEmpty()) {
                attr.setImageLittleUrlList(imageLittleUrlList);
            }

            if (null != listener) {
                attr.setOnPhotoChangeListener(listener);
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

                PhotoLayoutAttr.PhotoImageInfo photoImageInfo = new PhotoLayoutAttr.PhotoImageInfo();
                photoImageInfo.setImageDrawableIntrinsicWidth(imageDrawableIntrinsicWidth);
                photoImageInfo.setImageDrawableIntrinsicHeight(imageDrawableIntrinsicHeight);
                photoImageInfo.setImageX(imageX);
                photoImageInfo.setImageY(imageY);
                attr.addImageLocalInfo(photoImageInfo);
            }

            return new PhotoLayout(activity, attr);
        }
    }
}
