package top.itaso.lib.alog;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 崩溃 crash 信息记录，
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2021/9/11
 */
class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler instance;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    //最大文件数量，超过数量即删除旧日志文件
    private int limitLogCount = 5;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "init: [ALog-Content] Currently there is no write permission, you can only write after you request it no need to reinitialize it");
            FileUtils.isWritePermission = false;
        } else {
            FileUtils.isWritePermission = true;
        }
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, ">>>>>>error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);

        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        if (!FileUtils.isWritePermission) {
            Log.e(TAG, "handleException: [ALog-Content] Currently there is no write permission, you can only write after you request it and call ALog.init().resetWritePermissio(true) ");
            return true;
        }
        //控制日志文件数量
        limitAppLogCount();
        //保存日志文件
        writeCatchInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, ">>>>>>an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, ">>>>>>an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String writeCatchInfo2File(Throwable ex) {
        String path = ALog.init().getAlogRootPath();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            //日志文件名称
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                //发送给开发人员
//                sendCrashLog2PM(path + fileName);
                fos.close();
            }
            return fileName;
        } catch (FileNotFoundException fileNotFoundException) {
            Log.e(TAG, "write2File: [ALog-Content] log write failure,msg=" + fileNotFoundException.getMessage());
            FileUtils.isWritePermission = false;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 将捕获的导致崩溃的错误信息发送给开发人员
     * <p>
     * 目前只将log日志保存在sdcard 和输出到LogCat中，并未发送给后台。
     */
    private void sendCrashLog2PM(String fileName) {
        if (!FileUtils.isFileExists(fileName)) {
            Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
            while (true) {
                s = reader.readLine();
                if (s == null) break;
                //由于目前尚未确定以何种方式发送，所以先打出log日志。
                //TODO 发送文件到服务器
                Log.i(TAG, ">>>>>>" + s.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {   // 关闭流
            try {
                reader.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * limitLogCount 上限时删除最旧的一份 crash 文件
     */
    private void limitAppLogCount() {
        try {
            //如果用户没有自定义log的存储路径，则使用默认
            String path = ALog.init().getAlogRootPath();
            File file = new File(path);
            if (file != null && file.isDirectory()) {
                File[] files = file.listFiles(new CrashLogFliter());
                if (files != null && files.length > 0) {
                    Arrays.sort(files, comparator);
                    if (files.length > limitLogCount) {
                        for (int i = 0; i < (files.length - 1) - limitLogCount; i++) {
                            files[i].delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "limitAppLogCount>>>>>>" + e.getMessage());
        }
    }

    /**
     * 日志文件按文件日期升序排序
     */
    private Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File l, File r) {
            if (l.lastModified() > r.lastModified())
                return 1;
            if (l.lastModified() < r.lastModified())
                return -1;
            return 0;
        }
    };

    /**
     * 过滤.txt的文件
     */
    public class CrashLogFliter implements FileFilter {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".log"))
                return true;
            return false;
        }
    }
}
