package lib.kalu.photo.photo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import lib.kalu.bitmap.IntensifyImage;
import lib.kalu.bitmap.IntensifyImageView;
import lib.kalu.glide.util.GlideUtil;
import lib.kalu.photo.util.LogUtil;

final class PhotoAdapter extends PagerAdapter {

    private List<PhotoModel> list;
    private int begin;

    public PhotoAdapter(final List<PhotoModel> list, final int begin) {
        this.list = list;
        this.begin = begin;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 显示小图
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LogUtil.e("kalu", "instantiateItem");

        // step1
        final Context applicationContext = container.getContext().getApplicationContext();
        final PhotoModel photoModel = list.get(position);
        final String url = photoModel.getUrl();

        // step2
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;

        final PhotoViewBack image1 = new PhotoViewBack(applicationContext);
        image1.setLayoutParams(params);

        container.addView(image1);
        image1.setTag(image1.getId(), url);
        GlideUtil.loadImage(applicationContext, image1, url);

        return image1;
    }
}
