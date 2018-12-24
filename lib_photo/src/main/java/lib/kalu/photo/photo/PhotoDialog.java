package lib.kalu.photo.photo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import lib.kalu.photo.R;

/**
 * description: 全屏显示
 * created by kalu on 2018/4/20 10:18
 */
final class PhotoDialog extends Dialog {

    private OnBackPressedListener listener;

    public PhotoDialog(Activity activity) {
        super(activity, R.style.ShareDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {

        if (null != listener) {
            listener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.listener = listener;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
