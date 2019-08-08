import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import andy.dowanload.DownloadTask;
import andy.entity.FileInfo;
import andy.entity.ImageInfo;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 22:34
 * Description:
 */

public class Main {

    //定义的博客地址，与你自己Hexo发布的博客路径有关
    private static final String BASE_PATH = "/Users/xuwentao/Documents/test";
    private static final String IMAGE_REGEX = "^!\\[(.*)\\]\\((.*)\\)$";
    private static final String FORMAT = "{%% asset_img %1$s %2$s %%}";


    //包含图片链接信息的文章集合
    private static List<FileInfo> mFileInfoList = new ArrayList<>();


    public static void main(String[] args) throws InterruptedException {
        System.out.println("替换博客开始(≧▽≦)~啦啦啦!!!!");
        System.out.println("================================");
        analyseFile();
        doDownload();
        modifyBlog();
    }


    public static void analyseFile() {
        File dir = new File(BASE_PATH);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (!file.isDirectory() && fileName.endsWith("md")) {
                    System.out.println("【解析操作】---开始解析[" + fileName + "]中的图片链接");
                    prepareData(file);
                    System.out.println("【解析操作】---解析完毕-->[" + fileName + "]");
                }
            }
        } else {
            System.out.println("地址错误了，请检查文章路径地址");
        }

    }

    /**
     * 遍历文章，将有图片链接的文章放入集合中记录，同时并记录文章中的url
     */
    public static void prepareData(File file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            FileInfo fileInfo = new FileInfo(file);
            while ((line = br.readLine()) != null) {
                if (line.matches(IMAGE_REGEX)) {//通过正则，获取图片url
                    String[] split = line.split("]");
                    //获取原有文章中图片的url
                    String url = getDownloadURL(split[1]);
                    //构造图片信息对象
                    ImageInfo imageInfo = getImageInfoFromMD(split[0], removeFileSuffix(file), url);
                    fileInfo.addImageInfo(imageInfo);

                }
            }
            br.close();
            //如果文章含有图片信息，则添加到包含图片链接信息的文章集合
            if (fileInfo.havaImage()) {
                mFileInfoList.add(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取原有图片中的url
     */
    public static String getDownloadURL(String str) {
        if (str != null && !str.isEmpty()) {
            int first = str.indexOf('(') + 1;
            int pointIndex = str.indexOf(')');
            return str.substring(first, pointIndex);

        }
        return null;
    }


    /**
     * 从md 图片链接格式中，构建imageInfo信息
     *
     * @param mdLink         [xx.jpg]或[xx.png]或[xx.gif]
     * @param belongBlogName 所属博客名称
     * @return imageInfo信息
     */
    public static ImageInfo getImageInfoFromMD(String mdLink, String belongBlogName, String url) {
        ImageInfo imageInfo;
        if (mdLink != null && !mdLink.isEmpty()) {
            int first = mdLink.indexOf('[') + 1;
            int pointIndex = mdLink.indexOf('.');
            String imageFullName = mdLink.substring(first);
            String imageDesc = mdLink.substring(first, pointIndex);
            imageInfo = new ImageInfo(imageFullName, imageDesc, belongBlogName, url);
            return imageInfo;
        }
        return null;
    }

    public static void modifyBlog() {
        for (FileInfo fileInfo : mFileInfoList) {
            System.out.println("【替换操作】---开始替换[" + fileInfo.getName() + "]中的图片链接---");
            modifyFileContent(fileInfo);
            System.out.println("【替换操作】---替换完毕--->[" + fileInfo.getName() + "]中所有的链接已替换完毕---");
        }

    }

    /**
     * 将原来文章中的图片链接替换为Hexo图片链接格式,并重新创建文章。（将原来的文章删除）
     *
     * @param fileInfo 有图片链接的文章
     */
    public static void modifyFileContent(FileInfo fileInfo) {
        try {
            File file = fileInfo.getFile();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            File tempFile = new File(BASE_PATH, "tempFile");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile, true)));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(IMAGE_REGEX)) {
                    String[] split = line.split("]");
                    //原有文章的
                    String url = getDownloadURL(split[1]);
                    //获取文章中对应图片的信息
                    ImageInfo imageInfo = fileInfo.getImageInfoByUrl(url);
                    //替换文本中出现的md格式的图片连接
                    bw.write(String.format(FORMAT, imageInfo.getImageFullName(), imageInfo.getImageDesc()));
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            br.close();
            bw.close();
            //删除原有的文件并创建新的文件夹
            String path = file.getPath();
            file.delete();
            tempFile.renameTo(new File(path));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 去除文件夹的后缀名
     */
    public static String removeFileSuffix(File file) {
        String[] split = file.getName().split(".md");
        return split[0];
    }

    /**
     * 下载所有包含图片链接的文章中的图片，并将下载好的图片放入与文章同名的文件夹中
     */
    public static void doDownload() {
        for (FileInfo fileInfo : mFileInfoList) {
            for (ImageInfo imageInfo : fileInfo.getImageInfoList()) {
                DownloadTask downloadTask = new DownloadTask(imageInfo.getUrl(), imageInfo.getImageFullName(), BASE_PATH + File.separator + imageInfo.getBelongFile()) {
                    @Override
                    protected void onPreExecute() {
                        System.out.println("【图片下载】----开始下载[" + imageInfo.getBelongFile() + "]中的-->" + imageInfo.getImageFullName());
                    }

                    @Override
                    protected void onPostExecute() {
                        System.out.println("【图片下载】----下载完毕--->[" + imageInfo.getBelongFile() + "]----" + imageInfo.getImageFullName());
                    }
                };
                downloadTask.execute();
            }

        }

    }


}
