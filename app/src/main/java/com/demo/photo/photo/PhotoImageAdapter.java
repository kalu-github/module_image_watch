package com.demo.photo.photo;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.demo.photo.APP;
import com.demo.photo.util.GlideUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

final class PhotoImageAdapter extends PagerAdapter {

    private Activity mActivity;
    private PhotoLayout mPhotoLayout;
    private PhotoLayoutAttr mPhotoLayoutAttr;

    public PhotoImageAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setImageAttr(PhotoLayoutAttr mPhotoLayoutAttr){
        this.mPhotoLayoutAttr = mPhotoLayoutAttr;
    }

    public void setImageLayout(PhotoLayout mPhotoLayout){
        this.mPhotoLayout = mPhotoLayout;
    }

    @Override
    public int getCount() {
        return mPhotoLayoutAttr.getImageCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Context applicationContext = container.getContext().getApplicationContext();
        final PhotoImageView newImage = new PhotoImageView(applicationContext);
        newImage.setImageUrl(mPhotoLayoutAttr.getImageLittleUrlList().get(position));
        newImage.setImaageLongPressSave(mPhotoLayoutAttr.isImaageLongPressSave());
        newImage.setPhotoImageLayout(mPhotoLayout);
        newImage.setOnImageChangeListener(mPhotoLayoutAttr.getOnPhotoChangeListener());

        String littleUrl = mPhotoLayoutAttr.getImageLittleUrlList().get(position);
        GlideUtil.loadImageOriginal(mActivity, newImage, littleUrl, null);

        // 2. 设置默认属性
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        newImage.setLayoutParams(params);
        container.addView(newImage);

        return newImage;
    }
}
