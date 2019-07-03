import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import andy.dowanload.DownloadTask;
import andy.entity.ImageInfo;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 22:34
 * Description:
 */

public class Main {

    //定义的博客地址，与你自己hexo发布的博客路径有关
    private static final String BASE_PATH = "/Users/andy/test";
    private static final String IMAGE_REGEX = "^!\\[(.*)\\]\\((.*)\\)$";
    private static final String FORMAT = "{%% asset_img %1$s %2$s %%}";

    private static HashMap<String, ImageInfo> mMap = new LinkedHashMap<>();
    private static List<File> mFilelist = new ArrayList<>();


    public static void main(String[] args) throws InterruptedException {
        System.out.println("替换博客开始(≧▽≦)~啦啦啦!!!!");
        System.out.println("================================");
        analyseFile();
        doDownload();
        modifyBlog();
        System.out.println("================================");
        System.out.println("好了~~结束啦。快去查看你的文件吧");
    }


    public static void analyseFile() {
        File dir = new File(BASE_PATH);
        File[] files = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (!file.isDirectory() && fileName.endsWith("md")) {
                System.out.println("<===开始解析" + fileName + "中的图片链接===>");
                prepareData(file);
                System.out.println("O(∩_∩)O~" + fileName + "博客链接已经解析完毕O(∩_∩)O~");
            }
        }

    }

    public static void prepareData(File file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int imageLinkCount = 0;
            while ((line = br.readLine()) != null) {
                if (line.matches(IMAGE_REGEX)) {
                    String[] split = line.split("]");
                    String url = getDownloadURL(split[1]);
                    ImageInfo imageInfo = getImageInfoFromMD(split[0], removeFileSuffix(file), url);
                    mMap.put(UrlUtils.hashKeyFromUrl(url), imageInfo);
                    imageLinkCount++;
                }
            }
            //记录当前有图片链接的文件
            if (imageLinkCount > 0) {
                mFilelist.add(file);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyBlog() {
        for (File file : mFilelist) {
            System.out.println("<===开始替换" + file.getName() + "中的图片链接===>");
            modifyFileContent(file);
            System.out.println("O(∩_∩)O~" + file.getName() + "博客中所有的链接已替换完毕(∩_∩)O~");
        }

    }

    public static void modifyFileContent(File file) {
        try {
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
                    String url = getDownloadURL(split[1]);
                    ImageInfo imageInfo = mMap.get(UrlUtils.hashKeyFromUrl(url));
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
     * 从md 图片链接格式中，构建imageInfo信息
     *
     * @param mdLink         [xx.jpg]或[xx.png]或[xx.gif]
     * @param belongBlogName 所属博客名称
     * @return
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

    /**
     * 获取下载url
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
     * 去除文件夹的后缀名
     */
    public static String removeFileSuffix(File file) {
        String[] split = file.getName().split(".md");
        return split[0];
    }

    /**
     * 依次执行下载任务
     */
    public static void doDownload() {
        Set<String> keySet = mMap.keySet();
        for (String url : keySet) {
            ImageInfo imageInfo = mMap.get(url);
            DownloadTask downloadTask = new DownloadTask(imageInfo.getUrl(), imageInfo.getImageFullName(), BASE_PATH + File.separator + imageInfo.getBelongBlogName()) {
                @Override
                protected void onPreExecute() {
                    System.out.println("----开始下载" + imageInfo.getBelongBlogName() + "中的-->" + imageInfo.getImageFullName());
                }

                @Override
                protected void onPostExecute() {
                    System.out.println("----" + imageInfo.getImageFullName() + "下载完毕");

                }
            };
            downloadTask.execute();
        }
    }


}
