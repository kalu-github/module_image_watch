package lib.kalu.photo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * description: 当前类描述信息
 * created by Administrator on 2017/10/26 0026 上午 3:55
 */
public class PhotoDialog extends Dialog {

    public PhotoDialog(Activity baseActivity) {
        super(baseActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

    private OnBackPressedListener listener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.listener = listener;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
