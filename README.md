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
                         #imageview集合
                        .setImageList(image, image2, image3)
                         #大图集合
                        .setImageUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg")
                         #小图集合
                        .setImageLittleUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg")
                        .show();
```

todo:
\n1.添加手势，下滑渐渐退出
