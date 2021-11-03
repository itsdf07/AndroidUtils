package top.itaso.lib.alog;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Description ：ALog内容打印实现类
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2020/11/29
 */

class ALoggerImpl implements IALogger {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * 日期格式
     */
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
     * 保证Log不会错开打印
     */
    private static ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    /**
     * 详细 - 显示所有日志消息（默认值）
     */
    public static final int VERBOSE = 2;
    /**
     * 调试 - 显示仅在开发期间有用的调试日志消息，以及此列表中的消息级别较低
     */
    public static final int DEBUG = 3;
    /**
     * 信息 - 显示常规使用的预期日志消息以及此列表中的消息级别
     */
    public static final int INFO = 4;
    /**
     * 警告 - 显示尚未出现错误的可能问题，以及此列表中的消息级别
     */
    public static final int WARN = 5;
    /**
     * 错误 - 显示导致错误的问题，以及此列表中的消息级别较低
     */
    public static final int ERROR = 6;
    /**
     * 断言 - 显示开发人员期望永远不会发生的问题
     */
    public static final int ASSERT = 7;

    //绘制Log块的分割线
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    /**
     * ALog配置对象
     */
    private final ALogSettings mALogSettings = new ALogSettings();

    /**
     * Android 的单条Log打印最大限制是4096字节(4k)，所以这边限制Log块最大限制为1024个字符串长度，默认编码为UTF-8(汉子占3个字节长度)
     */
    private static final int CHUNK_LENGTH = 1024;

    /**
     * 从StackTraceElement[] 数据开始遍历的位置，值为自定义，影响遍历的次数
     */
    private static final int MIN_STACK_OFFSET = 2;


    @Override
    public ALogSettings getALogSettings() {
        return mALogSettings;
    }

    @Override
    public void v(String tag, String message, Object... args) {
        log(tag, VERBOSE, null, message, args);
    }

    @Override
    public void d(String tag, String message, Object... args) {
        log(tag, DEBUG, null, message, args);
    }

    @Override
    public void i(String tag, String message, Object... args) {
        log(tag, INFO, null, message, args);
    }

    @Override
    public void w(String tag, String message, Object... args) {
        log(tag, WARN, null, message, args);
    }

    @Override
    public void e(String tag, Throwable throwable, String message, Object... args) {
        log(tag, ERROR, throwable, message, args);
    }

    @Override
    public void wtf(String tag, Throwable throwable, String message, Object... args) {
        log(tag, ASSERT, throwable, message, args);
    }

    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d(getALogSettings().getTag(), "Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(4);
                d(getALogSettings().getTag(), LINE_SEPARATOR + message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(4);
                d(getALogSettings().getTag(), LINE_SEPARATOR + message);
                return;
            }
            e(getALogSettings().getTag(), null, "Invalid Json");
        } catch (JSONException e) {
            e(getALogSettings().getTag(), e, "Invalid Json");
        }
    }

    @Override
    public synchronized void log(String tag, int priority, String message, Throwable throwable) {
        if (getALogSettings().getLogLevel() == ALogLevel.NONE) {
            return;
        }
        if (null != throwable) {
            if (null != message) {
                message += ":" + ALogHelper.getStackTraceString(throwable);
            } else {
                message = ALogHelper.getStackTraceString(throwable);
            }
        }

        if (TextUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        int methodCount = getALogSettings().getMethodCount();

        logTopBorder(priority, tag);
        logThreadContent(priority, tag, methodCount);

        message = "[ALog-Content] " + message;
        int messageLength = message.length();
        //内容打印：
        if (messageLength <= CHUNK_LENGTH) {//不需要分段输出的log信息
            if (methodCount > 0) {
                if (getALogSettings().isShowThreadInfo()) {
                    logDivider(priority, tag);//需要打印线程信息时的分割线
                }
            }
            logContent(priority, tag, message);
            logBottomBorder(priority, tag);
            return;
        }
        if (methodCount > 0) {
            if (getALogSettings().isShowThreadInfo()) {
                logDivider(priority, tag);
            }
        }
        for (int i = 0; i < messageLength; i += CHUNK_LENGTH) {//分次打印过长log信息
            int count = Math.min(messageLength - i, CHUNK_LENGTH);
            logContent(priority, tag, message.substring(i, i + count));
        }

        logBottomBorder(priority, tag);
    }

    /*********************************************************************************************/


    /**
     * 这个方法必须同步的，以避免混乱的日志顺序。
     *
     * @param tag       TAG
     * @param priority  Log打印级别：v、d、i、w、e、a
     * @param throwable 异常
     * @param msg       打印内容
     * @param args      打印内容参数
     */
    private synchronized void log(String tag, int priority, Throwable throwable, String msg, Object... args) {
        if (getALogSettings().getLogLevel() == ALogLevel.NONE) {
            return;
        }
        String message = createMessage(msg, args);
        log(tag, priority, message, throwable);
    }


    /**
     * 利用 String.format() 的字符串常规类型格式化的重载方式 进行 msg 信息拼接
     *
     * @param message
     * @param args
     * @return
     */
    private String createMessage(String message, Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }

    /**
     * Log块上边界线╔══════════════════════════════
     *
     * @param logType
     * @param tag
     */
    private void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_BORDER);
    }

    /**
     * 打印当前Log所在线程 和 方法调用栈顺序
     *
     * @param logType     Log打印级别：v、d、i、w、e、a
     * @param tag         TAG
     * @param methodCount 显示Log调用方法栈顺序的数量
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    private void logThreadContent(int logType, String tag, int methodCount) {
        if (!getALogSettings().isShowThreadInfo()) {
            return;
        }
        //打印线程名称，如 Thread:main
        logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + "Thread:" + Thread.currentThread().getName());
        //绘制线程名称与方法调用信息之间的分割线
        logDivider(logType, tag);
        String level = "";

//        result = {StackTraceElement[17]@5284}
//        0 = {StackTraceElement@5289} "dalvik.system.VMStack.getThreadStackTrace(Native Method)"
//        1 = {StackTraceElement@5290} "java.lang.Thread.getStackTrace(Thread.java:1556)"
//        2 = {StackTraceElement@5291} "com.itsdf07.lib.alog.ALoggerImpl.logThreadContent(ALoggerImpl.java:274)"
//        3 = {StackTraceElement@5292} "com.itsdf07.lib.alog.ALoggerImpl.log(ALoggerImpl.java:185)"
//        4 = {StackTraceElement@5293} "com.itsdf07.lib.alog.ALoggerImpl.log(ALoggerImpl.java:225)"
//        5 = {StackTraceElement@5294} "com.itsdf07.lib.alog.ALoggerImpl.d(ALoggerImpl.java:97)"
//        6 = {StackTraceElement@5295} "com.itsdf07.lib.alog.ALog.dTag(ALog.java:109)"
//        7 = {StackTraceElement@5296} "com.itsdf07.app.alog.MainActivity$1.onClick(MainActivity.java:20)"
//        8 = {StackTraceElement@5297} "android.view.View.performClick(View.java:6291)"
//        9 = {StackTraceElement@5298} "android.view.View$PerformClick.run(View.java:24931)"
//        10 = {StackTraceElement@5299} "android.os.Handler.handleCallback(Handler.java:808)"
//        11 = {StackTraceElement@5300} "android.os.Handler.dispatchMessage(Handler.java:101)"
//        12 = {StackTraceElement@5301} "android.os.Looper.loop(Looper.java:166)"
//        13 = {StackTraceElement@5302} "android.app.ActivityThread.main(ActivityThread.java:7425)"
//        14 = {StackTraceElement@5303} "java.lang.reflect.Method.invoke(Native Method)"
//        15 = {StackTraceElement@5304} "com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:245)"
//        16 = {StackTraceElement@5305} "com.android.internal.os.ZygoteInit.main(ZygoteInit.java:921)"
        /**
         * Thread.currentThread().getStackTrace()
         * 返回一个表示该线程堆栈转储的堆栈跟踪元素数组。
         * 如果该线程尚未启动或已经终止，则该方法将返回一个零长度数组。
         * 如果返回的数组不是零长度的，则其第一个元素代表堆栈顶，它是该序列中最新的方法调用。
         * 最后一个元素代表堆栈底，是该序列中最旧的方法调用。getStackTrace()[0]表示的事getStackTrace方法
         */
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        //从StackTraceElement[] trace中倒序输出方法堆栈中的终止索引
        int stackOffset = getStackOffset(trace) + getALogSettings().getMethodOffset();

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        /**
         * 如上面StackTraceElement[]信息中，methodCount=2，stackOffset=6，那么下列for循环的内容则为
         * 从索引值=8的地方开始反序输出信息，即分别依次输出索引值为 8 和 7 的堆栈信息
         */
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString());
        }
    }

    /**
     * Log块下边界线╚════════════════════════════════════════════════════════════════════════════════════════
     *
     * @param logType
     * @param tag
     */
    private void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_BORDER);
    }

    /**
     * 横线绘制："════════════════════════════════════════════";
     *
     * @param logType Log打印级别：v、d、i、w、e、a
     * @param tag     TAG
     */
    private void logDivider(int logType, String tag) {
        logChunk(logType, tag, MIDDLE_BORDER);
    }


    /**
     * message内容块打印：使用者想要打印的内容
     *
     * @param logType
     * @param tag
     * @param chunk
     */
    private void logContent(int logType, String tag, String chunk) {
        if (!getALogSettings().isShowThreadInfo()) {//解读可参考logThreadContent中的逻辑解读
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            int stackOffset = getStackOffset(trace) + getALogSettings().getMethodOffset() - 1;
            int methodCount = getALogSettings().getMethodCount();
            //corresponding method count with the current stack may exceeds the stack trace. Trims the count
            if (methodCount + stackOffset > trace.length) {
                methodCount = trace.length - stackOffset - 1;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = methodCount; i > 1; i--) {
                int stackIndex = i + stackOffset;
                if (stackIndex >= trace.length) {
                    continue;
                }

                builder.append(trace[stackIndex].getMethodName())
                        .append(" (")
                        .append(trace[stackIndex].getFileName())
                        .append(":")
                        .append(trace[stackIndex].getLineNumber())
                        .append(") ");

            }
            chunk = builder.toString() + chunk;
        }
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logWrite2File("[" + logType(logType) + "]:" + tag + "::" + chunk + "\n", true);
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    /**
     * Log内容打印
     *
     * @param logType
     * @param tag
     * @param message
     */
    private void logChunk(int logType, String tag, String message) {
        if (TextUtils.isEmpty(tag)) {
            tag = getALogSettings().getTag();
        }
        switch (logType) {
            case ERROR:
                getALogSettings().getLogAdapter().e(tag, message);
                break;
            case INFO:
                getALogSettings().getLogAdapter().i(tag, message);
                break;
            case VERBOSE:
                getALogSettings().getLogAdapter().v(tag, message);
                break;
            case WARN:
                getALogSettings().getLogAdapter().w(tag, message);
                break;
            case ASSERT:
                getALogSettings().getLogAdapter().wtf(tag, message);
                break;
            case DEBUG:
                // Fall through, log debug by default
            default:
                getALogSettings().getLogAdapter().d(tag, message);
                break;
        }

//        logWrite2File("[" + logType(logType) + "] " + finalTag + "->" + message + "\n", true);
    }


    /**
     * 获取类名
     *
     * @param name
     * @return
     */
    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * StackTraceElement[] trace堆栈中，与ALog相关类无关开始索引点
     *
     * @param trace 堆栈信息
     * @return StackTraceElement[]堆栈中正序与ALog相关类中有关的最后索引，也可理解为反序中与ALog相关类中无关的第一个索引
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            //过滤掉ALog相关类中的Log信息打印，防止Log混乱
            if (!name.equals(ALoggerImpl.class.getName()) && !name.equals(ALog.class.getName())) {
                return --i;
                //例如以下trace中，进入该条件时i=7，最终返回6出去
//                result = {StackTraceElement[17]@5284}
//                0 = {StackTraceElement@5289} "dalvik.system.VMStack.getThreadStackTrace(Native Method)"
//                1 = {StackTraceElement@5290} "java.lang.Thread.getStackTrace(Thread.java:1556)"
//                2 = {StackTraceElement@5291} "com.itsdf07.lib.alog.ALoggerImpl.logThreadContent(ALoggerImpl.java:274)"
//                3 = {StackTraceElement@5292} "com.itsdf07.lib.alog.ALoggerImpl.log(ALoggerImpl.java:185)"
//                4 = {StackTraceElement@5293} "com.itsdf07.lib.alog.ALoggerImpl.log(ALoggerImpl.java:225)"
//                5 = {StackTraceElement@5294} "com.itsdf07.lib.alog.ALoggerImpl.d(ALoggerImpl.java:97)"
//                6 = {StackTraceElement@5295} "com.itsdf07.lib.alog.ALog.dTag(ALog.java:109)"
//                7 = {StackTraceElement@5296} "com.itsdf07.app.alog.MainActivity$1.onClick(MainActivity.java:20)"
//                8 = {StackTraceElement@5297} "android.view.View.performClick(View.java:6291)"
//                9 = {StackTraceElement@5298} "android.view.View$PerformClick.run(View.java:24931)"
//                10 = {StackTraceElement@5299} "android.os.Handler.handleCallback(Handler.java:808)"
//                11 = {StackTraceElement@5300} "android.os.Handler.dispatchMessage(Handler.java:101)"
//                12 = {StackTraceElement@5301} "android.os.Looper.loop(Looper.java:166)"
//                13 = {StackTraceElement@5302} "android.app.ActivityThread.main(ActivityThread.java:7425)"
//                14 = {StackTraceElement@5303} "java.lang.reflect.Method.invoke(Native Method)"
//                15 = {StackTraceElement@5304} "com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:245)"
//                16 = {StackTraceElement@5305} "com.android.internal.os.ZygoteInit.main(ZygoteInit.java:921)"
            }
        }
        return -1;
    }


    /**
     * 文件写入
     *
     * @param content 内容
     **/
    public synchronized void logWrite2File(String content, final boolean append) {
        if (!getALogSettings().isLog2Local()) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            return;
        }
        //如果用户没有自定义log的存储路径，则使用默认
        String logFilePath = getALogSettings().getDefineALogFilePath();
        if (TextUtils.isEmpty(logFilePath)) {
            logFilePath = getALogSettings().getDefaultALogFilePath();
        }
        final File logFile = FileUtils.getFileByPath(logFilePath);
        if (!FileUtils.createOrExistsFile(logFile)) {
            return;
        }
        Date now = new Date();
        String time = mSimpleDateFormat.format(now);
        final String logContent = time + ":" + content;

        Runnable syncRunnable = new Runnable() {
            @Override
            public void run() {
                FileUtils.write2File(logFile, logContent, append);
            }
        };
        mExecutorService.execute(syncRunnable);
    }

    /**
     * @param logType log级别
     * @return
     */
    private String logType(int logType) {
        String type;
        switch (logType) {
            case ERROR:
                type = "e";
                break;
            case INFO:
                type = "i";
                break;
            case VERBOSE:
                type = "v";
                break;
            case WARN:
                type = "w";
                break;
            case ASSERT:
                type = "wtf";
                break;
            case DEBUG:
                // Fall through, log debug by default
            default:
                type = "other";
                break;
        }
        return type;
    }
}
