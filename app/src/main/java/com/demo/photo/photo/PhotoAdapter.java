package com.demo.photo.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.demo.photo.util.GlideUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * description: 适配器
 * created by kalu on 2018/6/20 19:06
 */
final class PhotoAdapter extends PagerAdapter {

    private Activity mActivity;
    private PhotoLayout mPhotoLayout;
    private PhotoAttr mPhotoAttr;

    public PhotoAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setImageAttr(PhotoAttr mPhotoAttr) {
        this.mPhotoAttr = mPhotoAttr;
    }

    public void setImageLayout(PhotoLayout mPhotoLayout) {
        this.mPhotoLayout = mPhotoLayout;
    }

    @Override
    public int getCount() {
        return mPhotoAttr.getImageCount();
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
        final PhotoView newImage = new PhotoView(applicationContext);
        newImage.setOnPhotoChangeListener(mPhotoAttr.getOnPhotoChangeListener());
        String littleUrl = mPhotoAttr.getPhotoUrlLittle(position);
        GlideUtil.loadImageSimple(mActivity, newImage, littleUrl);

        // 2. 设置默认属性
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        newImage.setLayoutParams(params);
        container.addView(newImage);

        return newImage;
    }
}
