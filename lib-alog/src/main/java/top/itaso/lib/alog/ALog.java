package top.itaso.lib.alog;

/**
 * @Description ：ALog 是基于{@link android.util.Log}的封装，但是使用更简单，信息查看更强大
 * ALog.init()
 * .setLogLevel(ALogLevel.FULL) //是否打印log
 * .setTag("itsdf07") //自定义tag
 * .setDefineALogFilePath("xxx/xxx/xxx.log") //自定义log存储路径
 * .setLog2Local(true) //设置是否本地存储log记录
 * .setShowThreadInfo(true)//是否显示线程信息
 * .setMethodCount(2) //显示函数栈中的方法数
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2020/11/29
 */

public final class ALog {

    private static ALoggerImpl mALogger = new ALoggerImpl();

    public static ALogSettings init() {
        if (mALogger == null) {
            mALogger = new ALoggerImpl();
        }
        return mALogger.getALogSettings();
    }

    /**
     * 当前是否打印Log
     *
     * @return true-输出中，false-关闭中
     */
    public static boolean isLog() {
        return init().getLogLevel() == ALogLevel.FULL ? true : false;
    }


    /**
     * Verbose 日志打印，并且使用全局设置的TAG：.setTag("自定义TAG内容")
     * ALog.v("日志信息1 = %s, 日志信息1 = %s", 日志信息数据1, 日志数据信息2)
     * 如：ALog.v("isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void v(String message, Object... args) {
        vTag(mALogger.getALogSettings().getTag(), message, args);
    }

    /**
     * Verbose 日志打印，并且使用全局设置的TAG：.setTag("自定义TAG内容")
     * ALog.v("itsdf07", "日志信息1 = %s, 日志信息1 = %s", 日志信息数据1, 日志数据信息2)
     * ALog.v("itsdf07", "isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param tag     tag
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void vTag(String tag, String message, Object... args) {
        mALogger.v(tag, message, args);
    }

    public static void d(String message, Object... args) {
        dTag(mALogger.getALogSettings().getTag(), message, args);
    }

    public static void dTag(String tag, String message, Object... args) {
        mALogger.d(tag, message, args);
    }

    public static void i(String message, Object... args) {
        iTag(mALogger.getALogSettings().getTag(), message, args);
    }

    public static void iTag(String tag, String message, Object... args) {
        mALogger.i(tag, message, args);
    }

    public static void w(String message, Object... args) {
        wTag(mALogger.getALogSettings().getTag(), message, args);
    }

    public static void wTag(String tag, String message, Object... args) {
        mALogger.w(tag, message, args);
    }

    public static void e(String message, Object... args) {
        eTag(mALogger.getALogSettings().getTag(), message, args);
    }

    public static void eTag(String tag, String message, Object... args) {
        eTag(tag, null, message, args);
    }

    public static void eTag(String tag, Throwable throwable, String message, Object... args) {
        mALogger.e(tag, throwable, message, args);
    }

    public static void wtf(String message, Object... args) {
        wtfTag(mALogger.getALogSettings().getTag(), message, args);
    }

    public static void wtfTag(String tag, String message, Object... args) {
        wtfTag(tag, null, message, args);
    }

    public static void wtfTag(String tag, Throwable throwable, String message, Object... args) {
        mALogger.wtf(tag, throwable, message, args);
    }

    public static void json(String json) {
        mALogger.json(json);
    }
}
