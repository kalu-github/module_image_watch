[戳我下载 ==>](https://pan.baidu.com/s/1jHSbTx8)

![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-11-21-21-40-47-458_20171121214225.gif ) 
![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-12-24-20-59-09-590_20171224205953.gif ) 
![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-12-24-20-59-24-806_20171224205930.gif ) 

```
解决问题：

1.替换window实现方式
2.自动释放资源文件，不需要关心生命周期资源释放的类似问题
3.显示图片加载进度，不引入第三方控件，自定义view方式实现进度变化
```
```
使用方法：

new PhotoLayout.Builder(MainActivity.this)
                        .setPhotoLongPressSave(true) //长按保存图片到图库
                        .setPhotoOpenTransAnim(true) //渐变动画
                        .setPhotoBackgroundColor(Color.BLACK) //背景颜色
                        .setPhotoDefaultPosition(0) //默认索引位置
                        .setPhotoDefaultResource(R.mipmap.ic_launcher)  //默认占位图
                        .setPhotoList(image, image2, image3) //imageview集合
                        .setPhotoUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg") //大图集合
                        .setPhotoLittleUrlList("http://*.jpg", "http://*.jpg", "http://*.jpg") //小图集合
                        .show();
```
