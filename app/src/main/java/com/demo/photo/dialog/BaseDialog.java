package com.demo.photo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;

/**
 * description: 进场退场动画基类
 * created by kalu on 2016/11/9 10:34
 */
public abstract class BaseDialog extends Dialog {

    protected Activity baseActivity;

    public BaseDialog(Activity baseActivity, int themeResId) {
        super(baseActivity, themeResId);
        this.baseActivity = baseActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initView());
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initData();
    }

    /**
     * 初始化布局
     */
    protected abstract int initView();

    /**
     * 初始化操作
     */
    protected abstract void initData();
}