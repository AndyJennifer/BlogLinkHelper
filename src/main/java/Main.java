import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 22:34
 * Description:
 */

public class Main {

    //定义的博客地址，与你自己hexo发布的博客路径有关
    private static final String BASE_PATH = "/Users/xuwentao/Documents/test";
    private static final String IMAGE_REGEX = "^!\\[(.*)\\]\\((.*)\\)$";
    private static final String FORMAT = "{%% asset_img %1$s %2$s %%}";

    private static HashMap<String, ImageInfo> mMap = new LinkedHashMap<>();

    public static void main(String[] args) {
        System.out.println("替换博客开始(≧▽≦)啦啦啦！！！！！");
        readFile();
        doDownload();
        System.out.println("好了~~结束啦");
    }


    public static void readFile() {
        File dir = new File(BASE_PATH);
        File[] files = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (!file.isDirectory() && fileName.end("md")) {
                System.out.println("开始替换博客--->" + fileName + "中的图片链接");
                modifyFileContent(file);
                System.out.println(fileName + "--->博客链接已经替换完毕");
            }
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
                    ImageInfo imageInfo = getImageInfoFromMD(split[0], file);
                    String url = getDownloadURL(split[1]);
                    mMap.put(UrlUtils.hashKeyFromUrl(url), imageInfo);
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
     */
    public static ImageInfo getImageInfoFromMD(String str, File file) {
        ImageInfo imageInfo;
        if (str != null && !str.isEmpty()) {
            int first = str.indexOf('[') + 1;
            int pointIndex = str.indexOf('.');
            String imageFullName = str.substring(first);
            String imageDesc = str.substring(first, pointIndex);
            imageInfo = new ImageInfo(imageFullName, imageDesc, removeFileSuffix(file));
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
            ImageInfo imageInfo = mMap.get(keySet);
            DownloadTask downloadTask = new DownloadTask() {
                @Override
                protected void onPreExecute() {
                    System.out.println();
                }

            };
            downloadTask.execute(url, imageInfo.getImageFullName(), BASE_PATH + File.separator + imageInfo.getBelongBlog());
        }
    }


}
