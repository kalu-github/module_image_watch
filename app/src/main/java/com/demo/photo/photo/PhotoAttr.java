package com.demo.photo.photo;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 参数
 * created by kalu on 2018/6/20 19:05
 */
final class PhotoAttr {

    // 长按保存图片
    private boolean imaageLongPressSave = false;
    // 图片地址集合
    private List<? extends PhotoModel> photoModelList;
    // 图片原始位置信息集合
    private List<PhotoImageInfo> imageLocalInfoList;
    // 默认position
    private int imageDefaultPosition;
    // 背景颜色
    private int imageBackgroundColor;
    // 图片默认占位图
    private int imageDefaultResource;
    // 是否开启过度动画
    private boolean isOpenImageTransAnim = true;
    private OnPhotoChangeListener onPhotoChangeListener;

    public final String getPhotoUrl() {
        return photoModelList.get(imageDefaultPosition).getUrl();
    }

    public final String getPhotoUrl(int position) {
        return photoModelList.get(position).getUrl();
    }

    public final String getPhotoUrlLittle() {
        return photoModelList.get(imageDefaultPosition).getUrlLittle();
    }

    public final String getPhotoUrlLittle(int position) {
        return photoModelList.get(position).getUrlLittle();
    }

    public final void setPhotoModelList(List<? extends PhotoModel> photoModelList) {
        this.photoModelList = photoModelList;
    }

    public final int getImageDefaultPosition() {
        return imageDefaultPosition;
    }

    public final void setImageDefaultPosition(int imageDefaultPosition) {
        this.imageDefaultPosition = imageDefaultPosition;
    }

    public final int getImageBackgroundColor() {
        return imageBackgroundColor;
    }

    public final void setImageBackgroundColor(int imageBackgroundColor) {
        this.imageBackgroundColor = imageBackgroundColor;
    }

    public final int getImageDefaultResource() {
        return imageDefaultResource;
    }

    public final void setImageDefaultResource(int imageDefaultResource) {
        this.imageDefaultResource = imageDefaultResource;
    }

    public final boolean isOpenImageTransAnim() {
        return isOpenImageTransAnim;
    }

    public final void setOpenImageTransAnim(boolean openImageTransAnim) {
        isOpenImageTransAnim = openImageTransAnim;
    }

    public final OnPhotoChangeListener getOnPhotoChangeListener() {
        return onPhotoChangeListener;
    }

    public final void setOnPhotoChangeListener(OnPhotoChangeListener onPhotoChangeListener) {
        this.onPhotoChangeListener = onPhotoChangeListener;
    }

    public final boolean isImaageLongPressSave() {
        return imaageLongPressSave;
    }

    public final void setImageLongPressSave(boolean imaageLongPressSave) {
        this.imaageLongPressSave = imaageLongPressSave;
    }

    public final List<PhotoImageInfo> getImageLocalInfoList() {
        return imageLocalInfoList;
    }

    public final void setImageLocalInfoList(List<PhotoImageInfo> imageLocalInfoList) {
        this.imageLocalInfoList = imageLocalInfoList;
    }

    public final int getImageDrawableIntrinsicWidth(int position) {
        return imageLocalInfoList.get(position).getImageDrawableIntrinsicWidth();
    }

    public final int getImageDrawableIntrinsicHeight(int position) {
        return imageLocalInfoList.get(position).getImageDrawableIntrinsicHeight();
    }

    public final int getImageX(int position) {
        return imageLocalInfoList.get(position).getImageX();
    }

    public final int getImageY(int position) {
        return imageLocalInfoList.get(position).getImageY();
    }

    public final int getDefaultImageDrawableIntrinsicWidth() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageDrawableIntrinsicWidth();
    }

    public final int getDefaultImageDrawableIntrinsicHeight() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageDrawableIntrinsicHeight();
    }

    public final int getDefaultImageX() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageX();
    }

    public final int getDefaultImageY() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageY();
    }

    public final void addImageLocalInfo(PhotoImageInfo mPhotoImageInfo) {

        if (null == imageLocalInfoList) {
            imageLocalInfoList = new ArrayList<>();
        }

        imageLocalInfoList.add(mPhotoImageInfo);
    }

    public final int getImageCount() {

        if (null == photoModelList || photoModelList.size() == 0) {
            return 0;
        }

        int size = 0;
        for (PhotoModel temp : photoModelList) {
            if (temp.getUrl().startsWith("http")) {
                ++size;
            }
        }

        return size;
    }

    static final class PhotoImageInfo {

        // 图片真实显示背景drawable宽度
        private int imageDrawableIntrinsicWidth;
        // 图片真实显示背景drawable高度
        private int imageDrawableIntrinsicHeight;
        // 开始点击图片的x坐标
        private int imageX;
        // 开始点击图片的y坐标
        private int imageY;

        int getImageDrawableIntrinsicWidth() {
            return imageDrawableIntrinsicWidth;
        }

        void setImageDrawableIntrinsicWidth(int imageDrawableIntrinsicWidth) {
            this.imageDrawableIntrinsicWidth = imageDrawableIntrinsicWidth;
        }

        int getImageDrawableIntrinsicHeight() {
            return imageDrawableIntrinsicHeight;
        }

        void setImageDrawableIntrinsicHeight(int imageDrawableIntrinsicHeight) {
            this.imageDrawableIntrinsicHeight = imageDrawableIntrinsicHeight;
        }

        int getImageX() {
            return imageX;
        }

        void setImageX(int imageX) {
            this.imageX = imageX;
        }

        int getImageY() {
            return imageY;
        }

        void setImageY(int imageY) {
            this.imageY = imageY;
        }
    }
}
