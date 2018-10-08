package com.demo.photo.photo;

/**
 * description: 监听
 * created by kalu on 2018/6/20 19:06
 */
public abstract class OnPhotoChangeListener {

    protected abstract void onDelete(int position);

    // 长按
    protected void onLongPress(int position, String imageUrl) {
    }

    // 双击
    protected void onDoubleTap(int position, String imageUrl) {
    }

    // 单击
    protected void onSingleTap(int position, String imageUrl) {
    }

    // 拖拽
    protected void onDrag(float deltaX, float deltaY) {
    }

    // 缩放
    protected void onScale(float scale) {
    }

    protected void onRotate(float rotate, float focusX, float focusY) {
    }

    // 拖拽
    protected void onSave(String imagePath, int imageWidth, int imageHeight, int position) {
    }

    // 惯性滑动
    protected void onFling(float deltaX, float deltaY) {
    }
}
