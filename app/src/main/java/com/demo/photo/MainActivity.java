package com.demo.photo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.demo.photo.photo.PhotoLayout;
import com.demo.photo.util.GlideUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView image = findViewById(R.id.image);
        final ImageView image2 = findViewById(R.id.image2);
        final ImageView image3 = findViewById(R.id.image3);

        GlideUtil.loadImageSimple(this, image, "http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif");
        GlideUtil.loadImageSimple(this, image2, "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg");
        GlideUtil.loadImageSimple(this, image3, "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PhotoLayout.Builder(MainActivity.this)
                        .setPhotoLongPressSave(false)
                        .setPhotoOpenTransAnim(true)
                        .setPhotoBackgroundColor(Color.BLACK)
                        .setPhotoDefaultPosition(0)
                        .setPhotoDefaultResource(R.mipmap.ic_launcher)
                        .setPhotoViewList(image, image2, image3)
                        .setPhotoUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .setPhotoLittleUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .show();
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PhotoLayout.Builder(MainActivity.this)
                        .setPhotoLongPressSave(false)
                        .setPhotoOpenTransAnim(true)
                        .setPhotoBackgroundColor(Color.BLACK)
                        .setPhotoDefaultPosition(1)
                        .setPhotoDefaultResource(R.mipmap.ic_launcher)
                        .setPhotoViewList(image, image2, image3)
                        .setPhotoUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .setPhotoLittleUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .show();
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PhotoLayout.Builder(MainActivity.this)
                        .setPhotoLongPressSave(false)
                        .setPhotoOpenTransAnim(true)
                        .setPhotoBackgroundColor(Color.BLACK)
                        .setPhotoDefaultPosition(2)
                        .setPhotoDefaultResource(R.mipmap.ic_launcher)
                        .setPhotoViewList(image, image2, image3)
                        .setPhotoUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .setPhotoLittleUrlList("http://scimg.jb51.net/allimg/160905/2-160Z51P540H0.gif", "https://b-ssl.duitang.com/uploads/item/201511/18/20151118131629_RenHN.thumb.700_0.jpeg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .show();
            }
        });
    }
}
