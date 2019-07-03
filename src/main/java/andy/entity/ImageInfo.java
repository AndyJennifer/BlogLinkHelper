package andy.entity;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 23:50
 * Description:
 */

public class ImageInfo {

    private String imageFullName;//图片全称 如xx.png xx.jpg xx.gif
    private String imageDesc;//图片描述
    private String belongBlogName;//所属博客名称
    private String url;//图片下载地址


    public ImageInfo(String imageFullName, String imageDesc, String belongBlogName, String url) {
        this.imageFullName = imageFullName;
        this.imageDesc = imageDesc;
        this.belongBlogName = belongBlogName;
        this.url = url;
    }

    public String getImageFullName() {
        return imageFullName;
    }

    public void setImageFullName(String imageFullName) {
        this.imageFullName = imageFullName;
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public String getBelongBlogName() {
        return belongBlogName;
    }

    public void setBelongBlogName(String belongBlogName) {
        this.belongBlogName = belongBlogName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
