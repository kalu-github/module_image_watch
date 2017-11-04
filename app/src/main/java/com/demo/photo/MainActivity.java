package com.demo.photo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.demo.photo.photo.OnImageChangeListener;
import com.demo.photo.photo.OnImageChangeSimpleListener;
import com.demo.photo.photo.PhotoImageLayout;
import com.demo.photo.util.GlideUtil;
import com.demo.photo.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView image = findViewById(R.id.image);
        final ImageView image2 = findViewById(R.id.image2);
        final ImageView image3 = findViewById(R.id.image3);

        GlideUtil.loadImageSimple(this, image, "http://sjbz.fd.zol-img.com.cn/t_s1080x1920c/g5/M00/05/0D/ChMkJ1myURyIcWL0AAXIZtwWvIEAAgSQQJ3DjUABch-255.jpg");
        GlideUtil.loadImageSimple(this, image2, "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src");
        GlideUtil.loadImageSimple(this, image3, "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PhotoImageLayout.Builder(MainActivity.this)
                        .setImageLongPressSave(true)
                        .setImageOpenTransAnim(true)
                        .setImageBackgroundColor(Color.BLACK)
                        .setImageDefaultPosition(0)
                        .setImageDefaultResource(R.mipmap.ic_launcher)
                        .setImageList(image, image2, image3)
                        .setImageUrlList("http://sjbz.fd.zol-img.com.cn/t_s1080x1920c/g5/M00/05/0D/ChMkJ1myURyIcWL0AAXIZtwWvIEAAgSQQJ3DjUABch-255.jpg", "http://i2.download.fd.pchome.net/g1/M00/12/1E/ooYBAFb8ySeIEhaMABsXm3dLn7oAAC4ZAChMvkAGxez781.jpg", "http://i2.download.fd.pchome.net/g1/M00/12/1E/ooYBAFb8ySeIEhaMABsXm3dLn7oAAC4ZAChMvkAGxez781.jpg")
                        .setImageLittleUrlList("http://sjbz.fd.zol-img.com.cn/t_s1080x1920c/g5/M00/05/0D/ChMkJ1myURyIcWL0AAXIZtwWvIEAAgSQQJ3DjUABch-255.jpg", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src", "http://img-download.pchome.net/download/1k0/h1/4j/o4jbrz-fkz.jpg@0e_0o_1024w_768h_90q.src")
                        .show();
            }
        });
    }
}
