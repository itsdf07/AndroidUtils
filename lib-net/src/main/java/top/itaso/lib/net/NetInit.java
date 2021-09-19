package top.itaso.lib.net;

import android.content.Context;

/**
 * @Description: Net框架初始化入口
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/12/20
 */

//  NetInit.init(this)
//          .withApiHost(BASE_URL_LOCAL)
//          .withInterceptors(interceptors)
//          .configure();

public class NetInit {
    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT.name(), context.getApplicationContext());
        return Configurator.getInstance();
    }

    private static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    /**
     * 获取某个全局配置信息
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getConfiguration(String key) {
        return getConfigurator().getConfiguration(key);
    }

    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT.name());
    }

}
