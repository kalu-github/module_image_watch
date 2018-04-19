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

//    /**
//     * @param clockwise 顺时针方向
//     * @param position  当前pisition
//     * @param degree    旋转角度
//     */
    // void onRotate(boolean clockwise, int position, float degree);
    // void onRotate(float degree);
    void onRotate(float degrees, float focusX, float focusY);

    // 惯性滑动
    void onFling(float deltaX, float deltaY);
}
