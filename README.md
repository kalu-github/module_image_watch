![image](https://github.com/kaluzh/PhotoView_Demo/blob/master/Screenrecorder-2017-11-05.gif ) 

```
简书地址：

http://www.jianshu.com/p/36931e1d3c84

1.替换window实现方式
2.自动释放资源文件，不需要关心生命周期资源释放的类似问题
3.显示图片加载进度，不引入第三方控件，自定义view方式实现进度变化
```
```
使用方法：

new PhotoImageLayout.Builder(MainActivity.this)
                        .setImageLongPressSave(true) //长按保存图片到图库
                        .setImageOpenTransAnim(true) //渐变动画
                        .setImageBackgroundColor(Color.BLACK) //背景颜色
                        .setImageDefaultPosition(0) //默认索引位置
                        .setImageDefaultResource(R.mipmap.ic_launcher)  //默认占位图
                        .setImageList(image, image2, image3) //imageview集合
                        .setImageUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg") //大图集合
                        .setImageLittleUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg") //小图集合
                        .show();
```
```
todo:

1.添加手势，下滑渐渐退出
```
