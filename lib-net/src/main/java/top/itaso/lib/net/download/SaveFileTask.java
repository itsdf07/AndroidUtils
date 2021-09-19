package top.itaso.lib.net.download;

import android.os.AsyncTask;
import android.text.TextUtils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import top.itaso.lib.net.ConfigKeys;
import top.itaso.lib.net.NetInit;
import top.itaso.lib.net.callback.IProgress;
import top.itaso.lib.net.callback.IRequest;
import top.itaso.lib.net.callback.ISuccess;
import top.itaso.lib.net.log.LogUtils;
import top.itaso.lib.net.uitls.FileUtils;

/**
 * @Description: 文件下载的保存异步线程
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2021/7/21
 */
class SaveFileTask extends AsyncTask<Object, Long, File> {
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IProgress PROGRESS;

    public SaveFileTask(IRequest request, ISuccess success, IProgress progress) {
        this.REQUEST = request;
        this.SUCCESS = success;
        this.PROGRESS = progress;
    }

    /**
     * 开始执行后台线程时调用，当前处于UI线程
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LogUtils.logi("SaveFileTask onPreExecute: >>>>>>");
//        if (REQUEST != null){
//            REQUEST.onRequestStart();
//        }
    }

    /**
     * 开始执行后台线程任务，AsyncTask子线程
     */
    @Override
    protected File doInBackground(Object... params) {
        String destRootPath = (String) params[0];//全路径，如 /storage/emulated/0/Download
        String extension = (String) params[1];//下载文件的扩展名，如 .jpg
        ResponseBody body = (ResponseBody) params[2];
        String fileName = (String) params[3];//文件名，不包含扩展名，比如 aaaa.jpg 中，fileName 仅为 aaaa

        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }
        if (TextUtils.isEmpty(destRootPath)) {
            destRootPath = FileUtils.getInnerSDPath(NetInit.getConfiguration(ConfigKeys.NET_LOG.name())) + File.separator + "Download";
        }
        if (extension == null) {
            extension = "";
        }
        long totalLength = body.contentLength();
        InputStream is = body.byteStream();
        String filePath = destRootPath + File.separator + fileName + extension;
        LogUtils.logi("SaveFileTask doInBackground: >>>>>>totalLength=" + totalLength + ",filePath=" + filePath);
        // /storage/emulated/0/Download/aaaa.jpg
        File downloadFile = FileUtils.getFileByPath(filePath);
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(downloadFile);
            bos = new BufferedOutputStream(fos);

            byte data[] = new byte[1024 * 4];
            long currentLength = 0;
            long count;
            while ((count = bis.read(data)) != -1) {
                bos.write(data, 0, (int) count);
                currentLength += count;
                publishProgress(totalLength, currentLength);
            }
            bos.flush();
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return downloadFile;
    }

    /**
     * 刷新线程，调用publishProgress(...)时回调，一般用于刷新进度，比如doInBackground中调用publishProgress进行进度刷新,UI线程
     */
    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        LogUtils.logi("SaveFileTask onProgressUpdate: >>>>>>totalLength=" + values[0] + ",currentLength=" + values[1]);
        if (PROGRESS != null) {
            PROGRESS.onProgress(values[0], values[1]);
        }
    }

    /**
     * 后台线程结束时调用，当前处于UI线程
     */
    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (file != null) {
            LogUtils.logi("SaveFileTask onPostExecute: >>>>>>file=" + file.getAbsolutePath() + "," + file.length());
        }
        if (SUCCESS != null) {
            SUCCESS.onSuccess(file == null ? "" : file.getPath());
        }
        if (REQUEST != null && !isCancelled()) {
            REQUEST.onRequestEnd();
        }
    }

    @Override
    protected void onCancelled() {
        LogUtils.logi("SaveFileTask onCancelled: >>>>>>");
//        if (REQUEST != null) {
//            REQUEST.onRequestEnd();
//        }
    }
}
