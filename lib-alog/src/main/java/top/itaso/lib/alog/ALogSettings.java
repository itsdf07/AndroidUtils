package top.itaso.lib.alog;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description ：ALog的配置
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2020/11/29
 */

public final class ALogSettings {
    /**
     * 日期格式
     */
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd");
    /**
     * ALog的根路径
     */
    private final String alogRoot = "ALOG";
    /**
     * Log的TAG
     */
    private String TAG = "itsdf07";

    /**
     * 是否保存Log信息
     */
    private boolean isLog2Local = false;

    /**
     * 默认Log本地文件存储路径
     * Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
     */
    private final String defaultALogFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    /**
     * 自定义Log本地存储路径：需指向写入文件，如:xxx/xxx/xxx.log
     */
    private String defineALogFilePath = "";

    /**
     * 是否打印线程名称
     */
    private boolean isShowThreadInfo = false;

    /**
     * 日志控制：是否打印
     */
    private ALogLevel logLevel = ALogLevel.FULL;

    /**
     * 设置Log信息中打印的函数栈中的函数计数
     */
    private int methodCount = 2;
    /**
     * 负责设置StackTraceElement[]堆栈中函数信息打印索引控制
     */
    private int methodOffset = 0;

    private ALogAdapterImpl aLogAdapter;

    public String getAlogRoot() {
        return alogRoot;
    }

    public String getTag() {
        return TAG;
    }

    /**
     * 设置Log打印时的Tag
     *
     * @param tag
     * @return
     */
    public ALogSettings setTag(String tag) {
        if (null == tag) {
            throw new NullPointerException("TAG 不能设置 null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("TAG 不能设置为空字符串");
        }
        this.TAG = tag;
        return this;
    }

    /**
     * 是否开启本地保存Log信息
     *
     * @return
     */
    public boolean isLog2Local() {
        return isLog2Local;
    }

    /**
     * 设置是否本地保存Log信息
     *
     * @param isLog2Local
     */
    public ALogSettings setLog2Local(boolean isLog2Local) {
        this.isLog2Local = isLog2Local;
        return this;
    }

    /**
     * 默认Log路径Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ALOG" +  File.separator
     *
     * @return
     */
    public String getDefaultALogFilePath() {
        Date now = new Date();
        String date = mSimpleDateFormat.format(now);
        return defaultALogFilePath + getAlogRoot() + File.separator + date + ".log";
    }

    public String getDefineALogFilePath() {
        return defineALogFilePath;
    }

    /**
     * 自定义Log本地存储路径：需指向写入文件
     *
     * @param defineALogFilePath xxx/xxx/xxx.log
     */
    public ALogSettings setDefineALogFilePath(String defineALogFilePath) {
        this.defineALogFilePath = defineALogFilePath;
        return this;
    }

    /**
     * 是否显示线程名称：
     *
     * @return false：不显示线程信息，即只打印内容
     */
    public boolean isShowThreadInfo() {
        return isShowThreadInfo;
    }

    /**
     * 设置是否显示线程信息
     *
     * @param isShowThreadInfo
     * @return
     */
    public ALogSettings setShowThreadInfo(boolean isShowThreadInfo) {
        this.isShowThreadInfo = isShowThreadInfo;
        return this;
    }

    public ALogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * 设置是否打印Log信息
     *
     * @param logLevel
     * @return
     * @see ALogLevel
     */
    public ALogSettings setLogLevel(ALogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    /**
     * 设置Log信息中打印的函数栈中的函数计数
     *
     * @param methodCount
     */
    public ALogSettings setMethodCount(int methodCount) {
        this.methodCount = methodCount;
        return this;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    /**
     * 设置Log信息中打印函数栈的起始位置，即用于控制需要打印哪几个(methodCount)函数
     *
     * @param methodOffset
     */
    ALogSettings setMethodOffset(int methodOffset) {
        this.methodOffset = methodOffset;
        return this;
    }

    private ALogSettings setLogAdapter(ALogAdapterImpl adapter) {
        this.aLogAdapter = adapter;
        return this;
    }

    public ALogAdapterImpl getLogAdapter() {
        if (aLogAdapter == null) {
            aLogAdapter = new ALogAdapterImpl();
        }
        return aLogAdapter;
    }
}
