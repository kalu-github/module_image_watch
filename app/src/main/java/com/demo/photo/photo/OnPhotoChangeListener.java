package com.demo.photo.photo;

public interface OnPhotoChangeListener {

    // 长按
    void onLongPress(int position, String imageUrl);

    // 双击
    void onDoubleTap(int position, String imageUrl);

    // 单击
    void onSingleTap(int position, String imageUrl);

    // 拖拽
    void onDrag(float deltaX, float deltaY);

    // 缩放
    void onScale(float scale);

    // 当前图片旋转角度，顺时针为正，只能为0,90,180,270
     void onRotate(float degree);

    // 惯性滑动
    void onFling(float deltaX, float deltaY);
}
