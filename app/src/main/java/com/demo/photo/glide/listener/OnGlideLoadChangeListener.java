package com.demo.photo.glide.listener;

/**
 * description 图片加载进度监听
 * created by kalu on 2016/10/23 13:11
 */
public interface OnGlideLoadChangeListener {

    void onLoadChange(String imageUrl, long loadSize, long totalSize, long percent);
}
