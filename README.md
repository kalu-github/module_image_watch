##
## 预览图片
![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-11-21-21-40-47-458_20171121214225.gif ) 
![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-12-24-20-59-09-590_20171224205953.gif ) 
![image](https://github.com/153437803/PhotoView/blob/master/Screenrecorder-2017-12-24-20-59-24-806_20171224205930.gif ) 

##
## 适用场景
```
1.替换window实现方式
2.自动释放资源文件，不需要关心生命周期资源释放的类似问题
3.显示图片加载进度，不引入第三方控件，自定义view方式实现进度变化
```

## 
## 使用方法
```
final PhotoModel model1 = PhotoLayout.createModel(image1, url1, url1, R.mipmap.ic_launcher);
final PhotoModel model2 = PhotoLayout.createModel(image2, url2, url2, R.mipmap.ic_launcher);
final PhotoModel model3 = PhotoLayout.createModel(image3, url3, url3, R.mipmap.ic_launcher);

new PhotoLayout.Builder(MainActivity.this)
        .setPhotoOpenTransAnim(true)
        .setPhotoBackgroundColor(Color.BLACK)
        .setPhotoDefaultPosition(0)
        .setPhotoModelList(model1, model2, model3)
        .show();
```
