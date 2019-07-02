import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 22:34
 * Description:
 */

public class Main {
    static final String BASE_PATH = "/Users/xuwentao/Documents/GitHub/AndyJennifer.github.io/source/_posts";
    static final String IMAGE_REGEX = "^!\\[(.*)\\]\\((.*)\\)$";
    static final String FORMAT = "{%% asset_img %1$s %2$s %%}";
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        System.out.println("替换博客开始(≧▽≦)啦啦啦！！！！！");
        readFile();
    }


    public static void readFile() {
        File dir = new File(BASE_PATH);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().contains("md")) {
                System.out.println("开始解析要修改的博客---->" + file.getName());
                modifyFileContent(file);
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
                    String[] imageInfo = getReplaceFormatStr(split[0]);
                    String downloadURL = getDownloadURL(split[1]);
                    bw.write(String.format(FORMAT, imageInfo[0], imageInfo[1]));
                    //开始执行下载任务
                    executorService.execute(new downLoadTask(downloadURL, imageInfo[0], removeFileSuffix(file)));
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

    public static String[] getReplaceFormatStr(String str) {
        if (str != null && !str.isEmpty()) {
            int first = str.indexOf('[') + 1;
            int pointIndex = str.indexOf('.');
            String imageFullName = str.substring(first);
            String imageDesc = str.substring(first, pointIndex);
            String[] data = new String[2];
            data[0] = imageFullName;
            data[1] = imageDesc;
            return data;
        }
        return null;
    }

    /**
     * 获取下载信息
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

    private static class downLoadTask implements Runnable {

        private String urlString;
        private String filename;
        private String savePath;

        public downLoadTask(String urlString, String filename, String savePath) {
            this.urlString = urlString;
            this.filename = filename;
            this.savePath = savePath;
        }

        @Override
        public void run() {
            System.out.println("开始下载......" + filename);
            download(urlString, filename, savePath);
            System.out.println("下载结束......" + filename);
        }

        static void download(String urlString, String filename, String savePath) {
            try {
                URL url = new URL(urlString);

                URLConnection con = url.openConnection();
                con.setConnectTimeout(5 * 1000);
                InputStream is = con.getInputStream();

                byte[] bs = new byte[1024];
                int len;
                File sf = new File(BASE_PATH, savePath);
                if (!sf.exists()) {
                    sf.mkdirs();
                }
                OutputStream os = new FileOutputStream(new File(sf, filename));
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
