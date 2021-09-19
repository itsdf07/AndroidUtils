package top.itaso.lib.net.rtf2;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import top.itaso.lib.net.api.ApiService;
import top.itaso.lib.net.callback.IError;
import top.itaso.lib.net.callback.IFailure;
import top.itaso.lib.net.callback.IProgress;
import top.itaso.lib.net.callback.IRequest;
import top.itaso.lib.net.callback.ISuccess;
import top.itaso.lib.net.callback.RequestCallbacks;
import top.itaso.lib.net.download.DownloadHandler;

/**
 * @Description: 网络可用框架接口，提供给框架外使用的
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/4/2
 */

public class NetClient {
    private final HashMap<String, Object> PARAMS;
    private final String URL;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final IProgress PROGRESS;
    private final RequestBody BODY;

    private final String DESTROOTPATH;
    private final String EXTENSION;
    private final String FILENAME;


    public NetClient(HashMap<String, Object> params,
                     String url,
                     IRequest request,
                     ISuccess success,
                     IFailure failure,
                     IError error,
                     IProgress progress,
                     RequestBody body,
                     String destRootPath,
                     String extension,
                     String filename) {
        this.PARAMS = params;
        this.URL = url;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.PROGRESS = progress;
        this.BODY = body;

        this.DESTROOTPATH = destRootPath;  ///sdcard/XXXX.ext
        this.EXTENSION = extension;
        this.FILENAME = filename;
    }

    public static NetClientBuilder create() {
        return new NetClientBuilder();
    }

    /**
     * 开始实现真实的网络操作
     *
     * @param method
     */
    private void request(HttpMethod method) {
        final ApiService service = NetCreator.getRestService();
        Call call = null;
        if (REQUEST != null) {
            REQUEST.onRequestStart();
        }
        switch (method) {
            case GET:
                if (null == PARAMS || PARAMS.size() == 0) {
                    call = service.get(URL, new HashMap<>());
                } else {
                    call = service.get(URL, PARAMS);
                }
                break;
            case POST:
                if (null == PARAMS || PARAMS.size() == 0) {
                    call = service.post(URL, new HashMap<>());
                } else {
                    call = service.post(URL, PARAMS);
                }
                break;
            case POST_RAW:
                if (BODY != null) {
                    call = service.postRaw(URL, BODY);
                } else {
                    String body = "";
                    if (PARAMS != null && !PARAMS.isEmpty()) {
                        Iterator postIterator = PARAMS.entrySet().iterator();
                        JSONObject bodyJson = new JSONObject();
                        while (postIterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) postIterator.next();
                            if (entry.getValue() != null) {
                                String key = entry.getKey().toString();
                                String value = entry.getValue().toString();
                                try {
                                    bodyJson.put(key, value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        body = bodyJson.toString();
                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
                    call = service.postRaw(URL, requestBody);
                }
                break;
            default:
                break;
        }
        if (call != null) {
            call.enqueue(getReqeustCallback());
        }
    }

    private Callback<String> getReqeustCallback() {
        return new RequestCallbacks(REQUEST, SUCCESS, FAILURE, ERROR);
    }

    //各种请求
    public final void get() {
        request(HttpMethod.GET);
    }

    public final void post() {
        request(HttpMethod.POST);
    }

    /**
     * 该方式请求网络时NetClientBuilder无需同时设置 params 和 raw ，
     * 如果同时使用，则废弃 params 设置的参数
     */
    public final void postRaw() {
        request(HttpMethod.POST_RAW);
    }

    public final void download() {
        if (REQUEST != null) {
            REQUEST.onRequestStart();
        }
        new DownloadHandler(PARAMS,
                URL,
                REQUEST,
                SUCCESS,
                FAILURE,
                ERROR,
                PROGRESS,
                DESTROOTPATH,
                EXTENSION,
                FILENAME)
                .handleDownload();
    }

}







