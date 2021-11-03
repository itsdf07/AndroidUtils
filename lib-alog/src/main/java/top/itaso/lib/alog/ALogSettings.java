package top.itaso.lib.alog;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

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
    private final String defaultRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

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

    String getAlogRoot() {
        return alogRoot;
    }

    String getTag() {
        return TAG;
    }

    /**
     * 设置Log打印时的Tag
     *
     * @param tag
     * @return
     */
    public ALogSettings setTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            throw new NullPointerException("TAG 不能设置空字符串或者null");
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

    public void resetWritePermission(boolean isWritePermission) {
        FileUtils.isWritePermission = isWritePermission;
    }

    /**
     * 开启异常崩溃捕获
     *
     * @param context
     */
    public ALogSettings writeCrash(Context context) {
        if (context != null) {
            CrashHandler.getInstance().init(context);
        } else {
            try {
                throw new Exception("content is not null when you want writeCrash");
            } catch (Exception e) {
                Log.e(TAG, "setLog2Local: >>>>>>e=", e);
            }
            return this;
        }
        return this;
    }

    /**
     * 设置是否本地保存Log信息
     *
     * @param context 当 isLog2Local = true 时，context 不能为 null ,否则设置无效
     */
    public ALogSettings writeLog(Context context) {
        if (context == null) {
            try {
                throw new Exception("content is not null when you want writeLog");
            } catch (Exception e) {
                Log.e(TAG, "setLog2Local: >>>>>>e=", e);
            }
            return this;
        }
        this.isLog2Local = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "setLog2Local: [ALog-Content] Currently there is no write permission, you can only write after you request it no need to reinitialize it");
            FileUtils.isWritePermission = false;
            return this;
        }
        FileUtils.isWritePermission = true;
        return this;
    }

    /**
     * 默认Log路径Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ALOG" +  File.separator
     *
     * @return
     */
    String getDefaultALogFilePath() {
        Date now = new Date();
        String date = mSimpleDateFormat.format(now);
        return defaultRootPath + getAlogRoot() + File.separator + date + ".log";
    }

    String getDefineALogFilePath() {
        return defineALogFilePath;
    }

    String getAlogRootPath() {
        //如果用户没有自定义log的存储路径，则使用默认
        File file = FileUtils.getFileByPath(defineALogFilePath);
        if (file != null) {
            return file.getParent() + File.separator;
        } else {
            return defaultRootPath + getAlogRoot() + File.separator;
        }
    }

    /**
     * 自定义Log本地存储路径：需指向写入文件
     *
     * @param defineALogFilePath xxx/xxx/xxx.log
     */
    public ALogSettings setDefineALogFilePath(String defineALogFilePath) {
        if (!TextUtils.isEmpty(defineALogFilePath)) {
            File file = FileUtils.getFileByPath(defineALogFilePath);
            if (file != null) {
                this.defineALogFilePath = defineALogFilePath;
            }
        }
        return this;
    }

    /**
     * 是否显示线程名称：
     *
     * @return false：不显示线程信息，即只打印内容
     */
    boolean isShowThreadInfo() {
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

    int getMethodCount() {
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

    int getMethodOffset() {
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
