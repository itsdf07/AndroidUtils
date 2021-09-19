package top.itaso.lib.net.callback;

/**
 * @Description: 网络请求成功时的回调
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/12/20
 */
public interface ISuccess<T> {
    void onSuccess(T response);
}
