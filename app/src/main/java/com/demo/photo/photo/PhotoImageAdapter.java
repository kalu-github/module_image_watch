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

class PhotoImageAdapter extends PagerAdapter {

    private Activity mActivity;
    private PhotoImageLayout mPhotoImageLayout;
    private PhotoImageLayoutAttr mPhotoImageLayoutAttr;

    public PhotoImageAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setImageAttr(PhotoImageLayoutAttr mPhotoImageLayoutAttr){
        this.mPhotoImageLayoutAttr = mPhotoImageLayoutAttr;
    }

    public void setImageLayout(PhotoImageLayout mPhotoImageLayout){
        this.mPhotoImageLayout = mPhotoImageLayout;
    }

    @Override
    public int getCount() {
        return mPhotoImageLayoutAttr.getImageCount();
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

        Context applicationContext = APP.getInstance().getApplicationContext();
        final PhotoImageView newImage = new PhotoImageView(applicationContext);
        newImage.setImageUrl(mPhotoImageLayoutAttr.getImageLittleUrlList().get(position));
        newImage.setImaageLongPressSave(mPhotoImageLayoutAttr.isImaageLongPressSave());
        newImage.setPhotoImageLayout(mPhotoImageLayout);
        newImage.setOnImageChangeListener(mPhotoImageLayoutAttr.getOnImageChangeListener());

        String littleUrl = mPhotoImageLayoutAttr.getImageLittleUrlList().get(position);
        GlideUtil.loadImageOriginal(mActivity, newImage, littleUrl, null);

        // 2. 设置默认属性
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        newImage.setLayoutParams(params);
        container.addView(newImage);

        return newImage;
    }
}
