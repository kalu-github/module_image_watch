# PhotoView_Demo
大图浏览

1.替换window实现方式
2.自动释放资源文件，不需要关心生命周期资源释放的类似问题
3.显示图片加载进度，不引入第三方控件，自定义view方式实现进度变化

![image](https://github.com/kaluzh/PhotoView_Demo/blob/master/Screenrecorder-2017-11-05.gif ) 


```
使用方法：
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
```

todo:
\n1.添加手势，下滑渐渐退出
