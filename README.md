# PhotoView_Demo
大图浏览

1.替换window实现方式
2.自动释放资源文件，不需要关心生命周期资源释放的类似问题
3.显示图片加载进度，不引入第三方控件，自定义view方式实现进度变化

![image](https://github.com/kaluzh/PhotoView_Demo/blob/master/Screenrecorder-2017-11-05.gif ) 


使用方法：
------------------------------------------------------------------------------------------------------------------------------------------------
# 需要传入activity引用, 因为内部是通过dialog方式实现
new PhotoImageLayout.Builder(MainActivity.this)
                        # 当前点击的view
                        .setImageOriginal(view)
                        # 是否显示位置渐变动画
                        .setImageOpenTransAnim(true)
                        # 背景颜色
                        .setImageBackgroundColor(Color.BLACK)
                        # 多张图片浏览器, 默认显示的索引
                        .setImageDefaultPosition(0)
                        # 默认图片的加载占位图
                        .setImageDefaultResource(R.mipmap.ic_launcher)
                        # 所有大图的网络地址集合
                        .setImageUrlList("http://desk.fd.zol-img.com.cn/t_s2880x1800c5/g5/M00/0F/08/ChMkJlnN3IqIMvjnACk2KTLBky4AAg5GgHVbdsAKTZB607.jpg", "http://desk.fd.zol-img.com.cn/t_s2880x1800c5/g5/M00/0F/08/ChMkJlnN3IqIMvjnACk2KTLBky4AAg5GgHVbdsAKTZB607.jpg")
                        # 所有小图的网络地址集合
                        .setImageLittleUrlList("http://desk.fd.zol-img.com.cn/t_s1280x800c5/g5/M00/0F/08/ChMkJlnN3IqIMvjnACk2KTLBky4AAg5GgHVbdsAKTZB607.jpg", "http://desk.fd.zol-img.com.cn/t_s1280x800c5/g5/M00/0F/08/ChMkJlnN3IqIMvjnACk2KTLBky4AAg5GgHVbdsAKTZB607.jpg")
                        # 图片监听事件
                        .setOnImageClickChangeListener(new OnImageChangeSimpleListener() {
                            
                            # 拖拽事件
                            @Override
                            public void onDrag(float deltaX, float deltaY) {
                                LogUtil.e("kalu", "onDrag ==> deltaX = " + deltaX + ", deltaY = " + deltaY);
                            }

                            # 长按事件
                            @Override
                            public void onLongPress(int position, String imageUrl) {
                                LogUtil.e("kalu", "onLongPress ==> position = " + position + ", imageUrl = " + imageUrl);
                            }

                            # 双击事件
                            @Override
                            public void onDoubleTap(int position, String imageUrl) {
                                LogUtil.e("kalu", "onDoubleTap ==> position = " + position + ", imageUrl = " + imageUrl);
                            }

                            # 单击事件
                            @Override
                            public void onSingleTap(int position, String imageUrl) {
                                LogUtil.e("kalu", "onSingleTap ==> position = " + position + ", imageUrl = " + imageUrl);
                            }
                        })
                        # 显示大图
                        .show();

todo:
\n1.添加手势，下滑渐渐退出
