package top.itaso.lib.net.rtf2;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import top.itaso.lib.net.ConfigKeys;
import top.itaso.lib.net.NetInit;
import top.itaso.lib.net.api.ApiService;

/**
 * @Description: 实现与 Retrofit2 接口对接
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/4/2
 */

public final class NetCreator {
    /**
     * 产生一个全局的 Retrofit 客户端
     */
    private static final class RetrofitHolder {
        private static final String BASE_URL = NetInit.getConfiguration(ConfigKeys.API_HOST.name());
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(OKHttpHolder.OK_HTTP_CLIENT)//不使用默认的OK3，那么就自行重新设置设置okhttp
                .build();
    }

    /**
     * 自定义OK3的内容
     */
    private static final class OKHttpHolder {
        private static final int TIME_OUT = 30;
        private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)//设置请求超时时间
//                .addInterceptor(new HeadInterceptor())//请求头拦截器，可用于改写请求协议头的数据
//                .addInterceptor(new BodyInterceptor())//请求体拦截器，可用于改写body协议中的数据
//                .addInterceptor(new RetryInterceptor())//设置重定向拦截器，需要时开启使用
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//打印okhttp请求体日志
                .build();
    }

    //提供接口让调用者得到retrofit对象
    private static final class RestServiceHolder {
        private static final ApiService REST_SERVICE = RetrofitHolder.RETROFIT_CLIENT.create(ApiService.class);
    }

    /**
     * 获取对象
     */
    public static ApiService getRestService() {
        return RestServiceHolder.REST_SERVICE;
    }
}
