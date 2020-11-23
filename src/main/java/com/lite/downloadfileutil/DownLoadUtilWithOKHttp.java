//created by xuanwenchao for download file in android

package com.lite.downloadfileutil;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


//基于OKHttp来下载文件、为了让下载类流程实现更简单、目前一个实便只能同时存在一个下载任务、
// 多次调用start时，如果上一个任务还在 running 会直接返回失败、但并不影响正在执行的任务。
public class DownLoadUtilWithOKHttp {

    public static final String TAG = "DownLoadUtilWithOKHttp";

    private static DownLoadUtilWithOKHttp downloadUtil;
    private OnDownloadListener listener;
    private Boolean isRunning;
    private DWTask m_task;

    public static DownLoadUtilWithOKHttp getInstance() {
        if (downloadUtil == null) {
            downloadUtil = new DownLoadUtilWithOKHttp();
        }
        return downloadUtil;
    }

    public DownLoadUtilWithOKHttp() {
        isRunning = false;
    }

    //下载线程内部类
    private class DWTask extends AsyncTask<String, Integer, Boolean> {

        private String m_sUrl;
        private String m_sDestFileDir;
        private String m_sDestFileName;
        private Integer m_iPreProgress;
        private OkHttpClient okHttpClient;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //task 启动前初始化相关变量
            m_iPreProgress = -1;
            okHttpClient = new OkHttpClient();
        }

        //params： 当执行 execute 传入三个参数 url, destFileDir, destFileName
        //返回值为启动下载线程结果、并不表示下载的结果
        @Override
        protected Boolean doInBackground(String... params) {
            for (int i = 0; i < params.length; i++) {
                Log.i(TAG, "doInBackground-params[" + i + "]=" + params[i]);
            }

            this.m_sUrl = params[0];
            this.m_sDestFileDir = params[1];
            this.m_sDestFileName = params[2];

            Request request = new Request.Builder()
                    .url(m_sUrl)
                    .build();

            OkHttpClient client = new OkHttpClient();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                return false;
            }

            //发起新的异步请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 下载失败监听回调
                    listener.onDownloadFailed(e);
                }

                @Override
                public void onResponse(Call call, Response response) {

                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;

                    //储存下载文件的目录
                    File dir = new File(m_sDestFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, m_sDestFileName);

                    try {

                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);

                            //下载中的进度有变化时更新进度条
                            if(progress > m_iPreProgress) {
                                m_iPreProgress = progress;
                                publishProgress(progress);
                            }
                        }
                        fos.flush();
                        //下载完成
                        listener.onDownloadSuccess(file);
                    } catch (Exception e) {
                        listener.onDownloadFailed(e);
                    } finally {

                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            return true;
        }


        /**
         * 在主线程 显示线程任务执行的进度
         * @param progresses 当前的下载进度值0-100之间
         */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            listener.onDownloading(progresses[0]);
        }

        /**
         * 下载任务已经被取消
         */
        @Override
        protected void onCancelled() {
            isRunning = false;
            listener.onDownloadFailed(null);
        }
    }


    /**
     * 开始执行下载任务
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     * @return  Boolean    多次调用start时，如果上一个任务还在 running 会直接返回失败、但并不影响正在执行的任务。
     */
    public Boolean start(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {

        if (isRunning == true) {
            return false;
        }

        this.listener = listener;
        isRunning = true;
        m_task = new DWTask();
        m_task.execute(url, destFileDir, destFileName);
        return true;

    }

    /**
     * 取消正在执行的下载任务
     */
    public void cancel(){
        if(m_task != null){
            m_task.cancel(true);
        }
    }


    public interface OnDownloadListener {

        /**
         * 下载文件成功通知
         * @param file 下载成功后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度更新通知
         * @param progress  当前的下载进度值0-100之间
         */
        void onDownloading(int progress);

        /**
         * 下载失败或异常通知
         */
        void onDownloadFailed(Exception e);
    }
}


