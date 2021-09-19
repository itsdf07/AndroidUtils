package top.itaso.lib.net.rtf2;


import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import top.itaso.lib.net.callback.IError;
import top.itaso.lib.net.callback.IFailure;
import top.itaso.lib.net.callback.IProgress;
import top.itaso.lib.net.callback.IRequest;
import top.itaso.lib.net.callback.ISuccess;

/**
 * @Description: 提供给框架外使用时进行网络请求参数设置
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/5/30
 */

public class NetClientBuilder {
    /**
     * 表单提交方式时设置的网络请求参数内容
     */
    private HashMap<String, Object> mParams;
    /**
     * 网络请求的api接口，如 api/test/test2PostWithParam2
     */
    private String mUrl;
    private IRequest mRequest;
    private ISuccess mSuccess;
    private IFailure mFailure;
    private IError mError;
    private IProgress mProgress;
    private RequestBody mBody;

    private String mDestRootPath;
    private String mExtension;
    private String mFilename;

    NetClientBuilder() {

    }

    /**
     * 网络请求的api接口
     *
     * @param url 如 api/test/test2PostWithParam2
     * @return
     */
    public final NetClientBuilder url(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 表单提交方式时设置的网络请求参数内容
     *
     * @param params
     * @return
     */
    public final NetClientBuilder params(HashMap<String, Object> params) {
        this.mParams = params;
        return this;
    }


    /**
     * Json格式的请求数据
     * MediaType.parse("application/json;charset=UTF-8")
     *
     * @param raw
     * @return
     */
    public final NetClientBuilder raw(String raw) {
        this.mBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), raw);
        return this;
    }

    /**
     * 下载文件的根目录
     *
     * @param destRootPath
     * @return
     */
    public final NetClientBuilder destRootPath(String destRootPath) {
        this.mDestRootPath = destRootPath;
        return this;
    }

    public final NetClientBuilder extension(String extension) {
        this.mExtension = extension;
        return this;
    }

    public final NetClientBuilder fileName(String filename) {
        this.mFilename = filename;
        return this;
    }

    /**
     * 添加请求成功的回调
     *
     * @param success
     * @return
     */
    public final NetClientBuilder success(ISuccess success) {
        this.mSuccess = success;
        return this;
    }

    /**
     * 添加请求过程中的回调，比如请求开始、请求结束
     *
     * @param request
     * @return
     */
    public final NetClientBuilder request(IRequest request) {
        this.mRequest = request;
        return this;
    }

    /**
     * 添加属于网络请求错误返回时的回调，如服务器错误码的返回
     *
     * @param error
     * @return
     */
    public final NetClientBuilder error(IError error) {
        this.mError = error;
        return this;
    }

    public final NetClientBuilder progress(IProgress progress) {
        this.mProgress = progress;
        return this;
    }

    /**
     * 添加属于网络请求失败返回时的回调，如没有网络的失败返回
     *
     * @param failure
     * @return
     */
    public final NetClientBuilder failure(IFailure failure) {
        this.mFailure = failure;
        return this;
    }


    public final NetClient build() {
        return new NetClient(mParams, mUrl, mRequest, mSuccess, mFailure, mError, mProgress, mBody, mDestRootPath, mExtension, mFilename);
    }
}
