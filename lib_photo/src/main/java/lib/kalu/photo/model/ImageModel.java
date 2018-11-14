package lib.kalu.photo.model;

import lib.kalu.photo.photo.PhotoModel;

public final class ImageModel implements PhotoModel {

    private String url;
    private String littleUrl;

    private int imageWidth;
    private int imageHeight;

    private int drawableWidth;
    private int drawableHeight;

    private int drawableIntrinsicWidth;
    private int drawableIntrinsicHeight;

    private int imageX;
    private int imageY;

    public ImageModel(String url, String littleUrl,
                      int imageX, int imageY,
                      int imageWidth, int imageHeight,
                      int drawableWidth, int drawableHeight,
                      int drawableIntrinsicWidth, int drawableIntrinsicHeight) {

        this.url = url;
        this.littleUrl = littleUrl;

        this.imageX = imageX;
        this.imageY = imageY;

        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        this.drawableWidth = drawableWidth;
        this.drawableHeight = drawableHeight;

        this.drawableIntrinsicWidth = drawableIntrinsicWidth;
        this.drawableIntrinsicHeight = drawableIntrinsicHeight;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUrlLittle() {
        return littleUrl;
    }

    @Override
    public int getDrawableIntrinsicWidth() {
        return drawableIntrinsicWidth;
    }

    @Override
    public int getDrawableIntrinsicHeight() {
        return drawableIntrinsicHeight;
    }

    @Override
    public int getImageX() {
        return imageX;
    }

    @Override
    public int getImageY() {
        return imageY;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getDrawableWidth() {
        return drawableWidth;
    }

    public void setDrawableWidth(int drawableWidth) {
        this.drawableWidth = drawableWidth;
    }

    public int getDrawableHeight() {
        return drawableHeight;
    }

    public void setDrawableHeight(int drawableHeight) {
        this.drawableHeight = drawableHeight;
    }

    @Override
    public String toString() {
        return "imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", drawableWidth=" + drawableWidth +
                ", drawableHeight=" + drawableHeight +
                ", drawableIntrinsicWidth=" + drawableIntrinsicWidth +
                ", drawableIntrinsicHeight=" + drawableIntrinsicHeight +
                ", imageX=" + imageX +
                ", imageY=" + imageY +
                ", url='" + url + '\'' +
                ", littleUrl='" + littleUrl;
    }
}
