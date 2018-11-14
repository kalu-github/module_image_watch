package lib.kalu.photo.photo;

/**
 * description: 需要子类实现
 * created by kalu on 2018/6/20 19:06
 */
public interface PhotoModel {

    /**
     * 大图
     *
     * @return
     */
    String getUrl();

    /**
     * 小图
     *
     * @return
     */
    String getUrlLittle();

    /**
     * 图片真实显示背景drawable宽度
     */
    int getDrawableIntrinsicWidth();

    /**
     * 图片真实显示背景drawable高度
     */
    int getDrawableIntrinsicHeight();


    /**
     * 开始点击图片的x坐标, 左上角
     */
    int getImageX();

    /**
     * 开始点击图片的y坐标, 左上角
     */
    int getImageY();
}
