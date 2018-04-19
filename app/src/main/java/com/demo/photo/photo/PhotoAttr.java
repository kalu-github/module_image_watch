package com.demo.photo.photo;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 当前类描述信息
 * created by kalu on 2012017/11/4:20:57
 **/
final class PhotoAttr {

    // 长按保存图片
    private boolean imaageLongPressSave = false;
    // 大图集合
    private List<String> imageUrlList;
    // 小图集合
    private List<String> imageLittleUrlList;
    // 图片原始位置信息集合
    private List<ImageView> imageList;
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

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public int getImageDefaultPosition() {
        return imageDefaultPosition;
    }

    public void setImageDefaultPosition(int imageDefaultPosition) {
        this.imageDefaultPosition = imageDefaultPosition;
    }

    public List<ImageView> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageView> imageList) {
        this.imageList = imageList;
    }

    public int getImageBackgroundColor() {
        return imageBackgroundColor;
    }

    public void setImageBackgroundColor(int imageBackgroundColor) {
        this.imageBackgroundColor = imageBackgroundColor;
    }

    public int getImageDefaultResource() {
        return imageDefaultResource;
    }

    public void setImageDefaultResource(int imageDefaultResource) {
        this.imageDefaultResource = imageDefaultResource;
    }

    public boolean isOpenImageTransAnim() {
        return isOpenImageTransAnim;
    }

    public void setOpenImageTransAnim(boolean openImageTransAnim) {
        isOpenImageTransAnim = openImageTransAnim;
    }

    public OnPhotoChangeListener getOnPhotoChangeListener() {
        return onPhotoChangeListener;
    }

    public void setOnPhotoChangeListener(OnPhotoChangeListener onPhotoChangeListener) {
        this.onPhotoChangeListener = onPhotoChangeListener;
    }

    public List<String> getImageLittleUrlList() {
        return imageLittleUrlList;
    }

    public void setImageLittleUrlList(List<String> imageLittleUrlList) {
        this.imageLittleUrlList = imageLittleUrlList;
    }

    public boolean isImaageLongPressSave() {
        return imaageLongPressSave;
    }

    public void setImageLongPressSave(boolean imaageLongPressSave) {
        this.imaageLongPressSave = imaageLongPressSave;
    }

    public List<PhotoImageInfo> getImageLocalInfoList() {
        return imageLocalInfoList;
    }

    public void setImageLocalInfoList(List<PhotoImageInfo> imageLocalInfoList) {
        this.imageLocalInfoList = imageLocalInfoList;
    }

    public int getImageDrawableIntrinsicWidth(int position) {
        return imageLocalInfoList.get(position).getImageDrawableIntrinsicWidth();
    }

    public int getImageDrawableIntrinsicHeight(int position) {
        return imageLocalInfoList.get(position).getImageDrawableIntrinsicHeight();
    }

    public int getImageX(int position) {
        return imageLocalInfoList.get(position).getImageX();
    }

    public int getImageY(int position) {
        return imageLocalInfoList.get(position).getImageY();
    }

    public int getDefaultImageDrawableIntrinsicWidth() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageDrawableIntrinsicWidth();
    }

    public int getDefaultImageDrawableIntrinsicHeight() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageDrawableIntrinsicHeight();
    }

    public int getDefaultImageX() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageX();
    }

    public int getDefaultImageY() {
        return imageLocalInfoList.get(imageDefaultPosition).getImageY();
    }

    public void addImageLocalInfo(PhotoImageInfo mPhotoImageInfo) {

        if (null == imageLocalInfoList) {
            imageLocalInfoList = new ArrayList<>();
        }

        imageLocalInfoList.add(mPhotoImageInfo);
    }

    public int getImageCount() {

        if (null == imageUrlList) {
            return 0;
        }

        return imageUrlList.size();
    }

    /**
     * 默认起始图片——小图url
     */
    public String getImageOriginalUrl() {
        if (null == imageLittleUrlList || imageLittleUrlList.size() == 0) {
            return "";
        } else {
            return imageLittleUrlList.get(imageDefaultPosition);
        }
    }

    // 每一个图片位置信息
    static class PhotoImageInfo {

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
