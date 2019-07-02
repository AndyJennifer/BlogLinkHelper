/**
 * Author:  andy.xwt
 * Date:    2019-07-02 23:50
 * Description:
 */

public class ImageInfo {

    private String imageFullName;//图片全称 如xx.png xx.jpg xx.gif
    private String imageDesc;//图片描述
    private String belongBlog;//所属博客

    public ImageInfo(String imageFullName, String imageDesc, String belongBlog) {
        this.imageFullName = imageFullName;
        this.imageDesc = imageDesc;
        this.belongBlog = belongBlog;
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

    public String getBelongBlog() {
        return belongBlog;
    }

    public void setBelongBlog(String belongBlog) {
        this.belongBlog = belongBlog;
    }
}
