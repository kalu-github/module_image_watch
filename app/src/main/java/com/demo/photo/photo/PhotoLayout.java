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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.photo.util.DeviceUtil;
import com.demo.photo.util.GlideUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * description 大图加载
 * created by kalu on 2016/10/23 13:11
 */
public final class PhotoLayout extends FrameLayout implements ViewPager.OnPageChangeListener {

    private PhotoAttr attr;
    private boolean isPressedBack = false;
    private PhotoDialog mPhotoDialog;
    private Activity mActivity;
    private Activity activity;

    private final ImageView mImagePlaceholder = new ImageView(getContext().getApplicationContext());
    private final ViewPager viewPager = new ViewPager(getContext().getApplicationContext());
    private final ImageView saveButton = new ImageView(getContext().getApplicationContext());
    private final ImageView ratateButton = new ImageView(getContext().getApplicationContext());
    private final ImageView delButtom = new ImageView(getContext().getApplicationContext());
    private final TextView numImage = new TextView(getContext().getApplicationContext());
    private PhotoDotView mPhotoPointView = new PhotoDotView(getContext().getApplicationContext());

    private PhotoLayout(Activity activity, final PhotoAttr attr) {
        super(activity.getApplicationContext());
        this.activity = activity;

        setBackgroundColor(attr.getImageBackgroundColor());
        //   BarView.setColor(activity, attr.getImageBackgroundColor());
        this.attr = attr;
        this.mActivity = activity;

        // 1.3 创建对象
        int imageDrawableIntrinsicWidth = attr.getDefaultImageDrawableIntrinsicWidth();
        int imageDrawableIntrinsicHeight = attr.getDefaultImageDrawableIntrinsicHeight();
        int imageX = attr.getDefaultImageX();
        int imageY = attr.getDefaultImageY();

        boolean openImageTransAnim = attr.isOpenImageTransAnim();
        mImagePlaceholder.setVisibility(openImageTransAnim ? View.VISIBLE : View.INVISIBLE);
        addView(mImagePlaceholder);
        LayoutParams params = new LayoutParams(imageDrawableIntrinsicWidth, imageDrawableIntrinsicHeight);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.leftMargin = imageX;
        params.topMargin = imageY;
        // 1.2 位置参数
        mImagePlaceholder.setLayoutParams(params);
        GlideUtil.loadImageSimple(activity, mImagePlaceholder, attr.getPhotoUrlLittle());

        // 2.初始化ViewPager
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(attr.getImageCount() + 2);
        addView(viewPager);

        // 3.指示器
        if (attr.getImageCount() > 1) {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPhotoPointView.setVisibility(openImageTransAnim ? View.INVISIBLE : View.VISIBLE);
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.bottomMargin = 100;
            mPhotoPointView.setLayoutParams(layoutParams);
            mPhotoPointView.attach2ViewPage(viewPager, attr.getImageCount());
            addView(mPhotoPointView);
        }

        // 图片
        final float circleSize = 45 * getContext().getApplicationContext().getResources().getDisplayMetrics().density + 0.5f;
        LayoutParams params4 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) circleSize);
        params4.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params4.bottomMargin = (int) (circleSize * 0.5f);
        numImage.setLayoutParams(params4);
        numImage.setVisibility(View.INVISIBLE);
        numImage.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        final float testsize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6f, getContext().getApplicationContext().getResources().getDisplayMetrics());
        numImage.setTextSize(testsize);
        numImage.setText((attr.getImageDefaultPosition() + 1) + File.separator + attr.getImageCount());
        numImage.setTextColor(Color.WHITE);
        addView(numImage);

        // 3.轮播图设置数据
        PhotoAdapter photoAdapter = new PhotoAdapter(mActivity);
        photoAdapter.setImageAttr(attr);
        photoAdapter.setImageLayout(PhotoLayout.this);
        viewPager.setAdapter(photoAdapter);

        // 3.显示大图
        mPhotoDialog = new PhotoDialog(activity);
        LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPhotoDialog.setContentView(PhotoLayout.this, params3);
        mPhotoDialog.show();

        mPhotoDialog.setOnBackPressedListener(() -> {

            if (isPressedBack) return;
            animDismiss();
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
        animatorSet.setDuration(openImageTransAnim ? 200 : 0);
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
                if (null != ratateButton) {
                    ratateButton.setVisibility(View.VISIBLE);
                }

                if (null != saveButton) {
                    saveButton.setVisibility(View.VISIBLE);
                }

                if (null != delButtom) {
                    delButtom.setVisibility(View.VISIBLE);
                }

                if (null != numImage) {
                    numImage.setVisibility(View.VISIBLE);
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

        isPressedBack = true;
        setBackgroundColor(Color.TRANSPARENT);
        //   BarView.setColor(activity, Color.parseColor("#13ad67"));

        if (null != viewPager) {
            viewPager.setEnabled(false);
            viewPager.setVisibility(View.INVISIBLE);
        }

        if (null != delButtom) {
            delButtom.setVisibility(View.INVISIBLE);
        }

        if (null != numImage) {
            numImage.setVisibility(View.INVISIBLE);
        }

        if (null != ratateButton) {
            ratateButton.setVisibility(View.INVISIBLE);
        }

        if (null != saveButton) {
            saveButton.setVisibility(View.INVISIBLE);
        }

        final int position = viewPager.getCurrentItem();
        if (null != mImagePlaceholder) {
            GlideUtil.loadImageSimple(activity, mImagePlaceholder, attr.getPhotoUrl(position));
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
        animatorSet.setDuration(openImageTransAnim ? 200 : 0);
        animatorSet.setStartDelay(openImageTransAnim ? 50 : 0);
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

        if (null != numImage) {
            numImage.setText((position + 1) + File.separator + attr.getImageCount());
        }

        final ImageView image = (ImageView) viewPager.getChildAt(position);
        Object tag = image.getTag(image.getId());

        final String bigUrl = attr.getPhotoUrl(position);

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
            GlideUtil.loadImagePhoto(mActivity, image, bigUrl);
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
        private List<? extends PhotoModel> photoModelList;
        private List<ImageView> imageViews;
        private boolean imageLongPressSave;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setPhotoModelList(List<? extends PhotoModel> photoModelList) {
            this.photoModelList = photoModelList;
            return this;
        }

        public <T extends PhotoModel> Builder setPhotoModelList(T... photoModel) {
            this.photoModelList = Arrays.asList(photoModel);
            return this;
        }

        public Builder setPhotoViewList(List<ImageView> imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        public Builder setPhotoViewList(ImageView... imageViews) {
            this.imageViews = Arrays.asList(imageViews);
            return this;
        }

        public Builder setPhotoOpenTransAnim(boolean isOpenImageTransAnim) {
            this.isOpenImageTransAnim = isOpenImageTransAnim;
            return this;
        }

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

        public Builder setOnPhotoChangeListener(OnPhotoChangeListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setPhotoLongPressSave(boolean imageLongPressSave) {
            this.imageLongPressSave = imageLongPressSave;
            return this;
        }

        public void show() {

            final PhotoAttr attr = new PhotoAttr();
            if (photoModelList != null && !photoModelList.isEmpty()) {
                attr.setPhotoModelList(photoModelList);
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

                final ImageView imageOriginal = imageViews.get(i);
                if (null == imageOriginal) {
                    new RuntimeException("当前点击的图片, 不存在");
                    return;
                }

                int imageWidth = imageOriginal.getWidth();
                int imageHeight = imageOriginal.getHeight();

                final Drawable imageDrawable = imageOriginal.getDrawable();
                if (null == imageDrawable) {
                    new RuntimeException("当前点击的图片, 没有背景图片");
                    return;
                }

                int imageDrawableIntrinsicWidth = imageDrawable.getIntrinsicWidth();
                int imageDrawableIntrinsicHeight = imageDrawable.getIntrinsicHeight();

                final int[] local = new int[2];
                imageOriginal.getLocationInWindow(local);
                int imageX = local[0] + (imageWidth - imageDrawableIntrinsicWidth) / 2;
                int imageY = local[1] - DeviceUtil.getStatusHeight() + (imageHeight - imageDrawableIntrinsicHeight) / 2;

                PhotoAttr.PhotoImageInfo photoImageInfo = new PhotoAttr.PhotoImageInfo();
                photoImageInfo.setImageDrawableIntrinsicWidth(imageDrawableIntrinsicWidth);
                photoImageInfo.setImageDrawableIntrinsicHeight(imageDrawableIntrinsicHeight);
                photoImageInfo.setImageX(imageX);
                photoImageInfo.setImageY(imageY);
                attr.addImageLocalInfo(photoImageInfo);
            }
            new PhotoLayout(activity, attr);
        }
    }
}
