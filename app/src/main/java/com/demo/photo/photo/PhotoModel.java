package com.demo.photo.photo;

/**
 * description: 需要子类实现
 * created by kalu on 2018/6/20 19:06
 */
public interface PhotoModel {

    /**
     * 小图
     *
     * @return
     */
    String getUrl();

    /**
     * 大图
     *
     * @return
     */
    String getUrlLittle();
}
