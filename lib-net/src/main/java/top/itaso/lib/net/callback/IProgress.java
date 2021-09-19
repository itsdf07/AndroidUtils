package top.itaso.lib.net.callback;

/**
 * @Description: 文件下载进度回调
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2021/7/21
 */
public interface IProgress {
    /**
     * 文件下载进度
     *
     * @param totalLength
     * @param currentLength
     */
    void onProgress(long totalLength, long currentLength);
}
