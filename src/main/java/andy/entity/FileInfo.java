package andy.entity;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Author:  andy.xwt
 * Date:    2019-08-08 18:32
 * Description:文章信息
 */


public class FileInfo {

    private Set<ImageInfo> mImageInfoList;//包含的图片信息

    private File mFile;

    public FileInfo(File file) {
        mFile = file;
    }

    /**
     * 添加原有文章中的图片信息
     */
    public void addImageInfo(ImageInfo imageInfo) {
        if (imageInfo != null) {
            if (mImageInfoList == null) {
                mImageInfoList = new HashSet<>();
            }
            mImageInfoList.add(imageInfo);
        }
    }


    /**
     * 根据url获取对应ImageInfo信息
     */
    public ImageInfo getImageInfoByUrl(String url) {
        if (mImageInfoList != null) {
            for (ImageInfo imageInfo : mImageInfoList) {
                if (imageInfo.getUrl().equals(url)) {
                    return imageInfo;
                }
            }
        }
        return null;
    }

    public Set<ImageInfo> getImageInfoList() {
        return mImageInfoList;
    }

    public void setImageInfoList(Set<ImageInfo> imageInfoList) {
        mImageInfoList = imageInfoList;
    }

    public boolean havaImage() {
        return mImageInfoList != null && !mImageInfoList.isEmpty();
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public String getName() {
        return mFile.getName();
    }
}
