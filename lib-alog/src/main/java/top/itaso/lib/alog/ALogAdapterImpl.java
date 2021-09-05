package top.itaso.lib.alog;

import android.util.Log;

/**
 * @Description ：
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2020/11/29
 */
class ALogAdapterImpl implements IALogAdapter {
    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
    }
}
