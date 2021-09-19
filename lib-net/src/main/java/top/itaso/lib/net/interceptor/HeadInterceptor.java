package top.itaso.lib.net.interceptor;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import top.itaso.lib.net.ConfigKeys;
import top.itaso.lib.net.NetInit;
import top.itaso.lib.net.callback.IHeadParamsCallback;
import top.itaso.lib.net.log.LogUtils;

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/11/3
 */
class HeadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        LogUtils.logi("HeadInterceptor->intercept->请求url：" + request.url().host());
        final Request.Builder builder = request.newBuilder();
        IHeadParamsCallback headParamsCallback = NetInit.getConfiguration(ConfigKeys.INTERCEPTOR_PARAMS_HEADER.name());
        HashMap<String, String> headers = null;
        if (headParamsCallback != null) {
            headers = headParamsCallback.headParams();
        }
        if (headers != null) {
            Iterator iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() != null) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    builder.addHeader(key, value);
                }

            }
        }
        Response response = chain.proceed(builder.build());
        LogUtils.logi("HeadInterceptor->intercept->response.code：" + response.code());
        return response;
    }
}
