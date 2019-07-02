import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 23:01
 * Description:
 */

public class DownloadTask {

    private static final String LOG_TAG = "DownloadTask";


    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCont = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread("DowanloadTask #" + mCont.getAndIncrement());
        }
    };

    private static BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>(128);

    /**
     * 构建线程池
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    public static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;

    /**
     * 构建线性调度器，当下载任务来的时候，串行执行
     */
    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }


    public DownloadTask() {


    }

    public void execute(String url, String dirName, String savePath) {
        executeOnExecutor(sDefaultExecutor, url, dirName, savePath);
    }

    public void executeOnExecutor(Executor executor, String url, String fileName, String savePath) {
        onPreExecute();
        executor.execute(new WorkRunnable(url, fileName, savePath));
    }

    /**
     * 当下载任务执行之前方法
     */
    protected void onPreExecute() {

    }

    /**
     * 构建工作任务
     */
    private class WorkRunnable implements Runnable {

        private String urlStr;
        private String fileName;
        private String savePath;

        public WorkRunnable(String urlStr, String fileName, String savePath) {
            this.urlStr = urlStr;
            this.fileName = fileName;
            this.savePath = savePath;
        }

        @Override
        public void run() {
            try {
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                URL url = new URL(urlStr);
                URLConnection con = url.openConnection();
                con.setConnectTimeout(5 * 1000);
                InputStream is = con.getInputStream();

                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(new File(file, fileName));
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
