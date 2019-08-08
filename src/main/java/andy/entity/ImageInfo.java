package andy.entity;

import java.util.Objects;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 23:50
 * Description:
 */

public class ImageInfo {

    private String imageFullName;//图片全称 如xx.png xx.jpg xx.gif
    private String imageDesc;//图片描述
    private String belongFile;//所属文章名称
    private String url;//图片下载地址


    public ImageInfo(String imageFullName, String imageDesc, String belongFile, String url) {
        this.imageFullName = imageFullName;
        this.imageDesc = imageDesc;
        this.belongFile = belongFile;
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

    public String getBelongFile() {
        return belongFile;
    }

    public void setBelongFile(String belongFile) {
        this.belongFile = belongFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageInfo imageInfo = (ImageInfo) o;
        return Objects.equals(belongFile, imageInfo.belongFile) &&
                Objects.equals(url, imageInfo.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(belongFile, url);
    }
}
