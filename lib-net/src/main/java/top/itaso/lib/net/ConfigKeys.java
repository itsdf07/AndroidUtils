package top.itaso.lib.net;

/**
 * @Description: 定义统一配置全局相关信息的内容 Key （Net框架全局配置内容，当全局内容不变时，使用enum进行定义是最安全的）
 * 用在配置Configurator中CONFIGS的存储信息
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/12/20
 */
public enum ConfigKeys {
    /**
     * 接口主机,如http://192.168.3.3:8080/
     */
    API_HOST,
    /**
     * 拦截器
     */
    INTERCEPTOR,
    /**
     * 请求头拦截器设置的公共参数
     */
    INTERCEPTOR_PARAMS_HEADER,
    /**
     * 全局上下文
     */
    APPLICATION_CONTEXT,
    /**
     * Net框架Log
     */
    NET_LOG,
    /**
     * 网络接口调试:api(),98Debug(测试)
     */
    NET_DEBUG,
    /**
     * 初始化开始时标记为false，配置完成之后需标注为true，标明已经配置好全局信息，之后才能实现其他流程
     */
    CONFIG_READY
}
