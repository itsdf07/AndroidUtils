package top.itaso.lib.net.interceptor;


import java.io.IOException;
import java.net.UnknownHostException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import top.itaso.lib.net.log.LogUtils;
import top.itaso.lib.net.uitls.HostUtils;

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/11/3
 */
class RetryInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        try {
            response = chain.proceed(request);
            return response;
        } catch (IOException e) {
            LogUtils.logi("RetryInterceptor->intercept->请求异常e:" + e);
            if (e instanceof UnknownHostException) {
                //获取request的创建者builder
                Request.Builder builder = request.newBuilder();
                HttpUrl oldHttpUrl = request.url();
                if ("xy.uheixia.com".equals(HostUtils.filterHost(oldHttpUrl.host()))) {//98Debug
                    HttpUrl newFullUrl = oldHttpUrl
                            .newBuilder()
//                        .scheme(oldHttpUrl.scheme())
                            .host("xy.uheixia.com")
//                        .port(oldHttpUrl.port())
                            .build();
                    Request requestNew = builder.url(newFullUrl).build();
                    response = chain.proceed(builder.url(newFullUrl).build());
                } else {
                    throw e;
                }

            } else {
                throw e;
            }
        }
        LogUtils.logi("RetryInterceptor->intercept->请求结束，url:" + request.url());
        return response;
    }
}
