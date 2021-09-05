package top.itaso.lib.alog;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @Description: 文件相关便捷操作，其中所有API均会提供File和FilePath类型的参数
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Gitee https://gitee.com/itsdf07
 * @Date 2021/5/7
 */
class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public interface IFileComplateCallback {
        /**
         * 删除情况回调
         *
         * @param sucFiles     删除成功的文件
         * @param failureFiles 删除失败的文件
         */
        void onComplate(ArrayList<String> sucFiles, ArrayList<String> failureFiles);
    }

    public interface IFileReplaceListener {
        boolean onReplace(String sourecFile, String destParentPath);
    }

    /**
     * 判断 path 是否为【目录】
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(String path) {
        File file = getFileByPath(path);
        return isDir(file);
    }

    /**
     * 判断 File 是否为【目录】
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(File file) {
        return isFileExists(file) && file.isDirectory();
    }

    /**
     * 判断 path 是否为【普通文件】
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(String path) {
        return isFile(getFileByPath(path));
    }

    /**
     * 判断 File 是否为【普通文件】
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(File file) {
        return isFileExists(file) && file.isFile();
    }

    /**
     * 根据【文件路径】获取对应的【文件】
     *
     * @param path
     * @return 文件
     */
    public static File getFileByPath(String path) {
        return TextUtils.isEmpty(path) ? null : new File(path);
    }

    /**
     * 判断【文件/文件夹】是否存在
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFileExists(String path) {
        return isFileExists(getFileByPath(path));
    }

    /**
     * 判断【文件/文件夹】是否存在
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFileExists(File file) {
        return null != file && file.exists();
    }


    /**
     * 判断指定目录下是否有子文件
     *
     * @param path
     * @return {@code true}: 有<br>{@code false}: 没有
     */
    public static boolean hasChileFiles(String path) {
        return hasChileFiles(getFileByPath(path));
    }


    /**
     * 判断指定目录下是否有子文件
     *
     * @param file
     * @return {@code true}: 有<br>{@code false}: 没有
     */
    public static boolean hasChileFiles(File file) {
        if (null == file) {
            //路径不存在、不是目录
            return false;
        }
        File files[] = file.listFiles();
        if (null != files && files.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取路径下的所有文件集，使用时注意判 null
     *
     * @param path
     * @return {@code 文件列表}
     */
    public static File[] getFileList(String path) {
        File file = new File(path);
        File[] fileLists = file.listFiles();
        return fileLists;
    }


    /**
     * 根据文件路径
     * 返回文件修改时间
     *
     * @param path 文件路径
     * @return {@code 返回文件上次修改的时间}
     */
    public static long getLastModified(String path) {
        File file = getFileByPath(path);
        return file == null ? 0 : file.lastModified();
    }

    //------------------------------------------------------------------------------

    /**
     * 创建目标【目录】：如果父级目录不存在，则一同创建<br/>
     * 1、如果目标file已存在，则返回是否为路径<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param path
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String path) {
        File file = getFileByPath(path);
        return createOrExistsDir(file);
    }

    /**
     * 创建目标【目录】：如果父级目录不存在，则一同创建<br/>
     * 1、如果目标file已存在，则返回是否为路径<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param file
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        if (null == file) {
            return false;
        }
        if (file.exists()) {
            return file.isDirectory();
        }
        return file.mkdirs();
//        return null != file && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 创建目标【文件】
     * 1、如果目标file已存在，则返回是否为文件<br/>
     * 2、如果file不存在，则优先创建其父目录<br/>
     * 2.1、创建成功，则继续创建文件<br/>
     * 2.2、创建失败，则返回创建文件失败<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param path 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String path) {
        File file = getFileByPath(path);
        return createOrExistsFile(file);
    }

    /**
     * 创建目标【文件】
     * 1、如果目标file已存在，则返回是否为文件<br/>
     * 2、如果file不存在，则优先创建其父目录<br/>
     * 2.1、创建成功，则继续创建文件<br/>
     * 2.2、创建失败，则返回创建文件失败<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (null == file) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }

        if (!createOrExistsDir(file.getParentFile())) {//创建文件目录
            return false;
        }
        try {
            return file.createNewFile();//创建文件
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    //------------------------------------------------------------------------------


    /**
     * 在线程中删除【文件】
     *
     * @param sourceFilePaths 需要删除的垃圾文件(单个File)
     */
    public static void copyFiles(final ArrayList<String> sourceFilePaths, final String destFileParentPath, final boolean inThread, final IFileComplateCallback callback, IFileReplaceListener listener) {
        final ArrayList<String> sucFiles = new ArrayList<>();
        final ArrayList<String> failureFiles = new ArrayList<>();
        if (inThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String filePath : sourceFilePaths) {
                        realCopyFileToDestParentPath(filePath, destFileParentPath, sucFiles, failureFiles, listener);
                    }
                    if (callback != null) {
                        callback.onComplate(sucFiles, failureFiles);
                    }
                }
            }).start();
        } else {
            for (String filePath : sourceFilePaths) {
                realCopyFileToDestParentPath(filePath, destFileParentPath, sucFiles, failureFiles, listener);
            }
        }
    }

    //------------------------------------------------------------------------------

    /**
     * 重命名【文件】
     *
     * @param path    文件路径
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(String path, String newName) {
        File file = getFileByPath(path);
        return rename(file, newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(File file, String newName) {
        // 文件为空返回false
        if (null == file) {
            return false;
        }
        // 文件不存在返回false
        if (!file.exists()) {
            return false;
        }
        // 新的文件名为空返回false
        if (TextUtils.isEmpty(newName)) {
            return false;
        }
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists() && file.renameTo(newFile);
    }

    /**
     * ----------------------------------------------------
     *
     */

    /**
     * 批量删除指定路径下的所有文件及子文件
     *
     * @param file       需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     */
    public static void deleteFile(final File file, final boolean needRename, final boolean inThread, IFileComplateCallback callback) {
        if (!isFileExists(file)) {
            if (callback != null) {
                ArrayList<String> failureFiles = new ArrayList<>();
                if (null != file) {
                    failureFiles.add(file.getAbsolutePath());
                }
                callback.onComplate(new ArrayList<String>(), failureFiles);
            }
            return;
        }
        deleteFilePath(file.getAbsolutePath(), needRename, inThread, callback);
    }

    /**
     * 批量删除指定文件
     *
     * @param files      需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     */
    public static void deleteFiles(final ArrayList<File> files, final boolean needRename, final boolean inThread, IFileComplateCallback callback) {
        if (files == null) {
            if (callback != null) {
                callback.onComplate(new ArrayList<>(), new ArrayList<>());
            }
            return;
        }

        ArrayList<String> filePaths = new ArrayList<>();
        for (File file : files) {
            filePaths.add(file.getAbsolutePath());
        }
        deleteFilePaths(filePaths, needRename, inThread, callback);
    }

    /**
     * 批量删除指定路径下的所有文件及子文件
     *
     * @param path       需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     */
    public static void deleteFilePath(final String path, final boolean needRename, final boolean inThread, IFileComplateCallback callback) {
        if (!isFileExists(path)) {
            if (callback != null) {
                ArrayList<String> failureFiles = new ArrayList<>();
                if (!TextUtils.isEmpty(path)) {
                    failureFiles.add(path);
                }
                callback.onComplate(new ArrayList<String>(), failureFiles);
            }
            return;
        }
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(path);
        deleteFilePaths(filePaths, needRename, inThread, callback);
    }

    /**
     * 批量删除指定文件
     *
     * @param filePaths  需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     */
    public static void deleteFilePaths(final ArrayList<String> filePaths, final boolean needRename, final boolean inThread, IFileComplateCallback callback) {
        if (filePaths == null) {
            if (callback != null) {
                callback.onComplate(new ArrayList<>(), new ArrayList<>());
            }
            return;
        }
        doDeleteFiles(filePaths, needRename, inThread, callback);
    }


    /**
     * 在线程中删除【文件】
     *
     * @param filePaths  需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     */
    private static void doDeleteFiles(final ArrayList<String> filePaths, final boolean needRename, final boolean inThread, final IFileComplateCallback callback) {
        final ArrayList<String> sucFiles = new ArrayList<>();
        final ArrayList<String> failureFiles = new ArrayList<>();
        if (inThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String filePath : filePaths) {
                        realDeleteFilesAndChildFiles(filePath, needRename, sucFiles, failureFiles);
                    }
                    if (callback != null) {
                        callback.onComplate(sucFiles, failureFiles);
                    }
                }
            }).start();
        } else {
            for (String filePath : filePaths) {
                realDeleteFilesAndChildFiles(filePath, needRename, sucFiles, failureFiles);
            }
        }
    }

    /**
     * 删除指定路径下的文件及文件夹
     *
     * @param filePath 指定文件夹或者文件路径
     */
    private static void realDeleteFilesAndChildFiles(String filePath, boolean needRename, ArrayList<String> sucFiles, ArrayList<String> failureFiles) {
        if (!TextUtils.isEmpty(filePath) && isFileExists(filePath)) {
            try {
                Log.v(TAG, "realDeleteFilesAndChildFiles: >>>>>>will delete filePath=" + filePath + ",and will " + (needRename ? "rename" : "do not rename"));
                File file = new File(filePath);
                if (needRename) {
                    File fileRename = new File(filePath + System.currentTimeMillis());
                    if (file.renameTo(fileRename)) {
                        file = fileRename;
                    }
                    Log.v(TAG, "realDeleteFilesAndChildFiles: >>>>>>renamed filePaht=" + file.getAbsolutePath());
                }
                if (file.isDirectory()) {//如果指定的绝对路径是一个目录，即文件夹，那么取出该目录下的列表
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        String childPath = files[i].getAbsolutePath();
                        if (!TextUtils.isEmpty(childPath) && isFileExists(childPath)) {
                            realDeleteFilesAndChildFiles(childPath, needRename, sucFiles, failureFiles);
                        }
                    }
                } else {
                    if (file.delete()) {
                        sucFiles.add(filePath);
                    } else {
                        failureFiles.add(filePath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            failureFiles.add(filePath);
        }
    }

    private static void realCopyFileToDestParentPath(String sourceFilePath, String destParentPath, final ArrayList<String> pSucFiles, final ArrayList<String> pFailureFiles, IFileReplaceListener listener) {
        Log.d(TAG, "realCopyFileToDestParentPath: >>>>>>sourceFilePath=" + sourceFilePath + ",destParentPath=" + destParentPath);
        if (!isFileExists(sourceFilePath)) {//被拷贝文件不存在
            pFailureFiles.add(sourceFilePath);
            return;
        }
        if (!createOrExistsDir(destParentPath)) {//目标目录不存在或者创建失败
            pFailureFiles.add(sourceFilePath);
            return;
        }
        File sourceFile = getFileByPath(sourceFilePath);//这边的sourceFile一定不会为null
        if (sourceFile.getParent().equals(destParentPath)) {//目标文件位置无变化
            pSucFiles.add(sourceFilePath);
            return;
        }

        String destFilePath = destParentPath + File.separator + sourceFile.getName();
        if (isFileExists(destFilePath)) {//目标目录文件已存在
            if (listener == null || listener.onReplace(sourceFilePath, destParentPath)) {
                if (!sourceFile.delete()) {// unsuccessfully delete then return
                    pFailureFiles.add(sourceFilePath);
                    return;
                } else {
                    //TODO 往下继续执行copy动作，所以这边不能return
                }
            } else {
                pSucFiles.add(sourceFilePath);
                return;
            }
        }

        //TODO 执行File的copy动作
//        boolean isCopySuccess = copyFile(sourceFilePath, destFilePath);
        if (copyFile(sourceFilePath, destFilePath)) {
            pSucFiles.add(sourceFilePath);
        } else {
            pFailureFiles.add(sourceFilePath);
        }
    }

    /**
     * ----------------------------------------------------
     */

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
    public static final int FILESCALE_1024 = 1024;//文件大小单位换算比例

    /**
     * 调用此方法计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @param sizeType {@code B}: 1<br>{@code KB}: 2<br>{@code MB}: 3<br>{@code GB}: 4
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = getFileByPath(filePath);
        long blockSize = 0;
        if (null == file) {
            return blockSize;
        }
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = getFileByPath(filePath);
        long blockSize = 0;
        if (null == file) {
            return blockSize + "";
        }
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file == null) {
            return size;
        }
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {

            } finally {
                fis.close();
            }
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File file) throws Exception {
        long size = 0;
        if (file == null) {
            return size;
        }
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < FILESCALE_1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < (FILESCALE_1024 * FILESCALE_1024)) {
            fileSizeString = df.format((double) fileS / FILESCALE_1024) + "KB";
        } else if (fileS < (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)) {
            fileSizeString = df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024)) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / FILESCALE_1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024)));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 外部存储是否可用 (存在且具有读写权限)
     *
     * @return
     */

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// MEDIA_MOUNTED_READ_ONLY (SDcard存在，只可以进行读操作)
    }

    /**
     * 获取剩余可以空间总大小(不包括保留块部分)
     *
     * @return 文件系统的可用大小
     */

    public static long getAvailableSpace(String path) {
        StatFs stat = new StatFs(path);
        long availableSize;//剩余可用空间总大小
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableSize = blockSize * availableBlocks;
        } else {
            availableSize = stat.getAvailableBytes();
        }
        return availableSize;
    }

    /**
     * 获取可以空间总大小(不包括保留块部分)
     *
     * @return 文件系统的总大小
     */

    public static long getTotalSpace(String path) {
        StatFs stat = new StatFs(path);
        long totalSize;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalSize = blockSize * totalBlocks;
        } else {
            totalSize = stat.getTotalBytes();
        }

        return totalSize;
    }

    /**
     * 输出存储空间相关数据
     *
     * @param path
     * @return
     */
    public static String queryStorageSpace(String path) {
        StatFs stat = new StatFs(path);

        long blockSize;      //每个block 占字节数
        long totalBlocks;   //存储块总数量
        long availableCount;//可用存储块数
        long freeBlocks;//剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long totalSize;//可用空间总大小
        long freeTotalSize;//文件系统上可用的块总数
        long availableSize;//剩余可用空间大小
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableCount = stat.getAvailableBlocks();
            freeBlocks = stat.getFreeBlocks();
            freeTotalSize = blockSize * freeBlocks;

            totalSize = blockSize * totalBlocks;
            availableSize = blockSize * availableCount;
        } else {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableCount = stat.getAvailableBlocksLong();
            freeBlocks = stat.getFreeBlocksLong();

            //API level 18（JELLY_BEAN_MR2）引入
            totalSize = stat.getTotalBytes();
            availableSize = stat.getAvailableBytes();
            freeTotalSize = stat.getFreeBytes();
        }
        StringBuilder storageSpaceInfo = new StringBuilder();

        storageSpaceInfo.append(path + " 存储信息：\n");
        storageSpaceInfo.append("单个存储块大小:" + formatFileSize(blockSize) + "\n");
        storageSpaceInfo.append("存储块总数:" + totalBlocks + "\n");
        storageSpaceInfo.append("可用存储块数:" + availableCount + "\n");
        storageSpaceInfo.append("可用存储块数(含保留块):" + freeBlocks + "\n");
        storageSpaceInfo.append("可用空间总大小:" + formatFileSize(totalSize) + "\n");
        storageSpaceInfo.append("剩余可用空间总大小(含保留块):" + formatFileSize(freeTotalSize) + "\n");
        storageSpaceInfo.append("剩余可用空间总大小:" + formatFileSize(availableSize) + "\n");
        return storageSpaceInfo.toString();
    }

    /**
     * ----------------------------------------------------
     */

    /**
     * 获取内置sd卡路径，<br/>
     * 目前个人认为可以移除就是外卡，不能移除就是内卡，只经过大量试验，没有任何依据！此处需慎重！！！
     *
     * @param context
     * @return 内卡路径
     */
    public static String getInnerSDPath(Context context) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {
            try {
                StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
                Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager);
                String state = null;
                String path = null;

                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath");
                    path = (String) getPath.invoke(volume);
                    state = (String) getVolumeState.invoke(manager, getPath.invoke(volume));

                    /**
                     * 是否可以移除(内置sdcard) TODO:
                     * 目前个人认为可以移除就是外卡，不能移除就是内卡，只经过大量试验，没有任何依据！此处需慎重！！！
                     */
                    Method isRemovable = volume.getClass().getMethod("isRemovable");
                    boolean removable = (Boolean) isRemovable.invoke(volume);

                    if (null != path && null != state && state.equals(Environment.MEDIA_MOUNTED)) {
                        if (false == removable) {
                            return path;
                        }
                    }
                }
            } catch (Exception e) {
                return "";
            }
        }
        // 得到存储卡路径
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡
        // 或可存储空间是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
            return sdDir.toString();
        }

        return "";
    }

    /**
     * 信息写入文件
     *
     * @param file    写入的文件
     * @param content 写入的数据
     * @param append  是否覆盖写入
     * @return {@code true}: 信息写入成功<br>{@code false}: 信息写入失败
     */
    public static boolean write2File(File file, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            content = "";
        }
        byte[] aData = content.getBytes();
        return write2File(file, aData, append);
    }

    /**
     * 信息写入文件
     *
     * @param file   写入的文件
     * @param aData  写入的数据
     * @param append 是否覆盖写入
     * @return {@code true}: 信息写入成功<br>{@code false}: 信息写入失败
     */
    private static boolean write2File(File file, byte[] aData, boolean append) {
        if (!createOrExistsFile(file)) {
            //写入文件不存在且创建失败时return false
            return false;
        }
        OutputStream out = null;
        boolean ok = false;
        try {
            out = new FileOutputStream(file, append);
            out.write(aData);
            ok = true;
        } catch (Exception e) {
            Log.e(TAG, "log write failure:" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "OutputStream close failure:" + e.getMessage());
                }
            }
        }
        return ok;
    }

    /**
     * 复制单个文件
     *
     * @param sourceFilePath String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param destFilePth    String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    private static boolean copyFile(String sourceFilePath, String destFilePth) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (!isFileExists(sourceFilePath) || !isFile(sourceFilePath)) {
                Log.e(TAG, "copyFile: >>>>>>sourceFilePath not exist");
                return false;
            }
            if (!createOrExistsFile(destFilePth)) {
                Log.e(TAG, "copyFile: >>>>>>destFilePth not exist");
                return false;
            }

            fileInputStream = new FileInputStream(sourceFilePath);
            fileOutputStream = new FileOutputStream(destFilePth);
            byte[] buffer = new byte[1024 * 2];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            boolean isInputStreamClose = true;
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    isInputStreamClose = false;
                }
            }
            boolean isOutputStreamClose = true;
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    isOutputStreamClose = false;
                }
            }
            if (isInputStreamClose && isOutputStreamClose) {
                return true;
            } else {
                return false;
            }
        }
    }

}
