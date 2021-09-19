package top.itaso.lib.net.download;

import android.os.AsyncTask;


import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.itaso.lib.net.callback.IError;
import top.itaso.lib.net.callback.IFailure;
import top.itaso.lib.net.callback.IProgress;
import top.itaso.lib.net.callback.IRequest;
import top.itaso.lib.net.callback.ISuccess;
import top.itaso.lib.net.log.LogUtils;
import top.itaso.lib.net.rtf2.NetCreator;

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2021/7/21
 */
public class DownloadHandler {
    private final HashMap<String, Object> PARAMS;
    private final String URL;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final IProgress PROGRESS;
    private final String DESTROOTPATH;
    private final String EXTENSION;
    private final String FILENAME;

    public DownloadHandler(HashMap<String, Object> params,
                           String url,
                           IRequest request,
                           ISuccess success,
                           IFailure failure,
                           IError error,
                           IProgress progress,
                           String destRootPath,
                           String extension,
                           String filename) {
        this.PARAMS = params;
        this.URL = url;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.PROGRESS = progress;
        this.DESTROOTPATH = destRootPath;
        this.EXTENSION = extension;
        this.FILENAME = filename;
    }

    public final void handleDownload() {
        HashMap<String, Object> params = PARAMS;
        if (params == null || params.isEmpty()) {
            LogUtils.logi("DownloadHandler handleDownload: >>>>>>help to init the params with map");
            params = new HashMap<>();
        }
        NetCreator.getRestService().download(URL, params)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            LogUtils.logi("DownloadHandler onResponse: >>>>>>call isExecuted=" + call.isExecuted());
                            //开始保存文件,开一个异步任务来做
                            SaveFileTask task = new SaveFileTask(REQUEST, SUCCESS, PROGRESS);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    DESTROOTPATH,
                                    EXTENSION,
                                    response.body(),
                                    FILENAME);
                            //如果下载完成
                            if (task.isCancelled()) {
                                if (REQUEST != null) {
                                    REQUEST.onRequestEnd();
                                }
                            }
                        } else {
                            LogUtils.logi("DownloadHandler onResponse: >>>>>>response is unsuccess:code=" + response.code() + ",msg=" + response.message());
                            if (ERROR != null) {
                                ERROR.onError(response.code(), response.message());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LogUtils.logi("DownloadHandler onFailure: >>>>>>response is failure:err=" + t.getMessage());
                        if (FAILURE != null) {
                            FAILURE.onFailure();
                        }
                    }
                });
    }
}
