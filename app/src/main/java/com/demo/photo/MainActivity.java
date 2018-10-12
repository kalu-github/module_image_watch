package com.demo.photo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.demo.photo.photo.PhotoLayout;
import com.demo.photo.photo.PhotoModel;
import com.demo.photo.util.GlideUtil;

import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String url1 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539334976897&di=e66dcc2fe0bf3e9c309de8c414b0152f&imgtype=0&src=http%3A%2F%2Fc5.biketo.com%2Fdata%2Fattachment%2Fforum%2F201805%2F29%2F135600knqyn99de9k8dqqx.gif";
        final String url2 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539335078007&di=b4bb77ae3dbc0724b2d13221906dd983&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F12%2F15%2F47%2F61bOOOPIC66_1024.jpg";
        final String url3 = "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src";

        final ImageView image1 = findViewById(R.id.image1);
        final ImageView image2 = findViewById(R.id.image2);
        final ImageView image3 = findViewById(R.id.image3);
        GlideUtil.loadImageSimple(this, image1, url1);
        GlideUtil.loadImageSimple(this, image2, url2);
        GlideUtil.loadImageSimple(this, image3, url3);

        image1.setOnClickListener(view -> {

            final PhotoModel model1 = PhotoLayout.createModel(image1, url1, url1, R.mipmap.ic_launcher);
            final PhotoModel model2 = PhotoLayout.createModel(image2, url2, url2, R.mipmap.ic_launcher);
            final PhotoModel model3 = PhotoLayout.createModel(image3, url3, url3, R.mipmap.ic_launcher);

            new PhotoLayout.Builder(MainActivity.this)
                    .setPhotoOpenTransAnim(true)
                    .setPhotoBackgroundColor(Color.BLACK)
                    .setPhotoDefaultPosition(0)
                    .setPhotoModelList(model1, model2, model3)
                    .show();
        });

        image2.setOnClickListener(v -> {

            final PhotoModel model1 = PhotoLayout.createModel(image1, url1, url1, R.mipmap.ic_launcher);
            final PhotoModel model2 = PhotoLayout.createModel(image2, url2, url2, R.mipmap.ic_launcher);
            final PhotoModel model3 = PhotoLayout.createModel(image3, url3, url3, R.mipmap.ic_launcher);

            new PhotoLayout.Builder(MainActivity.this)
                    .setPhotoOpenTransAnim(true)
                    .setPhotoBackgroundColor(Color.BLACK)
                    .setPhotoDefaultPosition(1)
                    .setPhotoModelList(model1, model2, model3)
                    .show();
        });

        image3.setOnClickListener(view -> {

            final PhotoModel model1 = PhotoLayout.createModel(image1, url1, url1, R.mipmap.ic_launcher);
            final PhotoModel model2 = PhotoLayout.createModel(image2, url2, url2, R.mipmap.ic_launcher);
            final PhotoModel model3 = PhotoLayout.createModel(image3, url3, url3, R.mipmap.ic_launcher);

            new PhotoLayout.Builder(MainActivity.this)
                    .setPhotoOpenTransAnim(true)
                    .setPhotoBackgroundColor(Color.BLACK)
                    .setPhotoDefaultPosition(2)
                    .setPhotoModelList(model1, model2, model3)
                    .show();
        });
    }
}
