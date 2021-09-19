package top.itaso.lib.net.callback;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.itaso.lib.net.log.LogUtils;

/**
 * @Description: 把自定义的接口回调绑定到Retrofit2上，可以实现精细内容的业务
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/12/20
 */

public class RequestCallbacks<T> implements Callback<T> {
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;

    public RequestCallbacks(IRequest request, ISuccess success, IFailure failure, IError error) {
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
    }


    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            if (call.isExecuted()) {//Call已经被执行了
                LogUtils.logi("RequestCallbacks onResponse: >>>>>>response is success:body=" + response.body());
                if (SUCCESS != null) {
                    SUCCESS.onSuccess(response.body());
                }
            }
        } else {
            LogUtils.logi("RequestCallbacks onResponse: >>>>>>response is unsuccess:code=" + response.code() + ",msg=" + response.message());
            if (ERROR != null) {
                ERROR.onError(response.code(), response.message());
            }
        }
        if (REQUEST != null) {
            REQUEST.onRequestEnd();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        LogUtils.logi("RequestCallbacks onFailure: >>>>>>response is failure:err=" + t.getMessage());
        if (FAILURE != null) {
            FAILURE.onFailure();
        }
        if (REQUEST != null) {
            REQUEST.onRequestEnd();
        }
    }
}
