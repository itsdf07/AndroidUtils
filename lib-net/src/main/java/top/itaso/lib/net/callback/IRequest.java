package top.itaso.lib.net.callback;

/**
 * @Description: 网络请求过程可以实现的事情
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/12/20
 */
public interface IRequest {
    /**
     * 如显示进度条、请求框之类的
     */
    void onRequestStart();

    void onRequestEnd();
}
