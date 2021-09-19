package top.itaso.lib.net.interceptor;


import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.itaso.lib.net.log.LogUtils;

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/11/3
 */
class BodyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        LogUtils.logi("BodyInterceptor->intercept->请求url：" + request.url());
        if (request.method().equals("POST")) {
            if (request.body() instanceof FormBody) {//表达提交
                LogUtils.logi("BodyInterceptor->intercept->表单请求");
            } else if (request.body() instanceof RequestBody) {
                LogUtils.logi("原始数据数据请求");
//                RequestBody requestBody = request.body();
//                Buffer buffer = new Buffer();
//                requestBody.writeTo(buffer);
//                ALog.dTag("okHttp", "body:%s", buffer.readByteString().utf8());
            }
        }
        Response response = chain.proceed(request);
        LogUtils.logi("BodyInterceptor->intercept->response.code：" + response.code());
        return response;
    }
}
