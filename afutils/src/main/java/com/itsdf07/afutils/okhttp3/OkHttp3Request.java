package com.itsdf07.afutils.okhttp3;

import android.text.TextUtils;


import com.itsdf07.afutils.log.FLog;
import com.itsdf07.afutils.okhttp3.callback.HttpBaseCallback;
import com.itsdf07.afutils.okhttp3.callback.HttpProgressCallback;
import com.itsdf07.afutils.okhttp3.impl.OkHttp3CallbackImpl;
import com.itsdf07.afutils.okhttp3.progress.ProgressRequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @Description ：
 * @Author itsdf07
 * @Time 2018/07/18
 */
class OkHttp3Request {

    public static final String TAG_HTTP = "tag_okhttp3";

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//JSON数据格式
    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");//二进制流数据

    private static Platform mPlatform;

    /**
     * @Description 请求方式
     */
    enum HttpMethodType {
        GET,
        POST,
    }

    private static OkHttpClient getOkHttpClient() {
        if (null == mPlatform) {
            mPlatform = Platform.get();
        }
        return OkHttp3Utils.getInstance().getOkHttpClient();
    }


    /**
     * 数据请求成功
     * 将http请求成功结果转至主线程
     *
     * @param tag
     * @param result
     * @param callback
     * @param isDecode
     */
    public static void sendSuccessResultCallback(final String tag, final String result, final OkHttp3CallbackImpl callback, final boolean isDecode) {
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    FLog.dTag(TAG_HTTP, "\nisDecode:%s\ntag:%s\nresult:%s", isDecode, tag, result);
                    callback.onSuccess(result, isDecode);
                    callback.onFinish();
                }

            }
        });
    }

    /**
     * 数据请求失败
     * 将http请求失败结果转至主线程
     *
     * @param netCode
     * @param callback
     */
    public static void sendFailResultCallback(final NetCode netCode, final OkHttp3CallbackImpl callback) {
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                FLog.eTag(TAG_HTTP, "NetCode:%s,\ndesc:%s,\ninfo:%s", netCode.getCode(), netCode.getDesc(), netCode.getInfo());
                if (callback != null) {
                    callback.onFailure(netCode.getCode(), netCode.getDesc());
                    callback.onFinish();
                }

            }
        });
    }

    /**
     * 默认不需要对返回的数据解密
     *
     * @param request  Request对象
     * @param callback 请求回调
     * @param isDecode 是否需要解密
     * @Description 异步请求
     */
    public static Call doEnqueue(final Request request, final OkHttp3CallbackImpl callback, final boolean isDecode) {
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NetCode netCode = NetCode.UNKNOW;
                if (e instanceof UnknownHostException) {
                    netCode = NetCode.CODE_6904;
                } else if (e instanceof SocketTimeoutException) {
//                    if (null != e.getMessage()) {
//                        if (e.getMessage().contains("failed to connect to")) {
//                            //TODO 连接超时
//                        }
//                        if (e.getMessage().equals("timeout")) {
//                            //TODO 读写超时
//                        }
//                    }
                    netCode = NetCode.CODE_6905;
                }
                sendFailResultCallback(netCode.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->:" + e.getMessage()), callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response) {
                    sendFailResultCallback(NetCode.CODE_6010.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response"), callback);
                    return;
                }
                if (null == response.body()) {
                    sendFailResultCallback(NetCode.CODE_6020.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response.body()"), callback);
                    return;
                }
                String result = response.body().string();
                if (TextUtils.isEmpty(result)) {
                    sendFailResultCallback(NetCode.CODE_6021.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->body is empty"), callback);
                    return;
                }
                try {
                    if (response.isSuccessful()) {
                        sendSuccessResultCallback(call.request().url().toString(), result, callback, isDecode);
                    } else {
                        sendFailResultCallback(NetCode.CODE_6011.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\tcode:" + response.code() + ",\n\t\terr->:" + response.message()), callback);
                    }
                } catch (Exception e) {
                    sendFailResultCallback(NetCode.CODE_6900.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->" + e.getMessage()), callback);
                }
            }
        });
        return call;
    }

    /****************************************************** V 2 ********************************************************/


    /**
     * 数据请求成功
     * 将http请求成功结果转至主线程
     *
     * @param result
     * @param callback
     */
    public static void sendSuccessResultCallback(final String tag, final String result, final HttpBaseCallback callback) {
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                FLog.dTag(TAG_HTTP, "\ntag:%s\nresult:%s", tag, result);
                if (null != callback) {
                    callback.onSuccess(result);
                }
            }
        });
    }

    /**
     * 数据请求失败
     * 将http请求失败结果转至主线程
     *
     * @param netCode
     * @param callback
     */
    public static void sendFailResultCallback(final NetCode netCode, final HttpBaseCallback callback) {
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                FLog.eTag(TAG_HTTP, "NetCode:%s,\ndesc:%s,\ninfo:%s", netCode.getCode(), netCode.getDesc(), netCode.getInfo());
                if (null != callback) {
                    callback.onFailure(netCode, netCode.getDesc());
                }
            }
        });
    }


    /**
     * 文件下载进度
     * 线程转为主线程
     *
     * @param currentLen
     * @param totalLen
     * @param callback
     * @param isFinish   是否下载完成
     */
    public static void sendProgressResultCallback(final long currentLen, final long totalLen, final HttpProgressCallback callback, final boolean isFinish) {
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                if (isFinish) {
                    FLog.dTag(TAG_HTTP, "下载完成");
                    callback.onSuccess("下载完成");
                } else {
                    FLog.dTag(TAG_HTTP, "下载中,totalLen:%s,currentLen:%s", totalLen, currentLen);
                    callback.onProgress(currentLen, totalLen);
                }
            }
        });
    }

    /**
     * @param params 请求参数
     * @Description RequestBody对象
     */
    private static RequestBody builderFormMapData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    /**
     * 获取存放上传文件的请求载体RequestBody，并实现进度数据透传
     *
     * @param file
     * @param callback
     * @return RequestBody对象
     */
    private static RequestBody builderFormFileData(File file, final HttpProgressCallback callback) {
        ProgressRequestBody progressRequestBody = null;
        if (null != file) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_STREAM, file);
            progressRequestBody = new ProgressRequestBody(requestBody, new ProgressRequestBody.Listener() {
                @Override
                public void onRequestProgress(final long byteWrited, final long contentLength) {
                    mPlatform.execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProgress(byteWrited, contentLength);
                        }
                    });
                }
            });
        }

        return progressRequestBody;
    }

    /*************************** builder request *****************************************************/
    /**
     * builder json/map 格式的data数据请求体：Request
     * <br/>V2
     *
     * @param methodType 请求方式
     * @param url        请求地址
     * @param params     请求参数
     * @param json       json数据格式
     * @Description Request对象
     */
    public static Request builderDataRequest(HttpMethodType methodType, String url, Map<String, String> params, String json) {
        Request request;
        Request.Builder builder = new Request.Builder().url(url);

        String bodyLog = "";
        if (methodType == HttpMethodType.POST) {
            if (json != null) {
                bodyLog = json;
                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
                builder.post(body);
            } else {
                RequestBody body = builderFormMapData(params);
                builder.post(body);
                bodyLog = buildBodyLog4Map(params);
            }
        } else if (methodType == HttpMethodType.GET) {
            builder.get();
            bodyLog = "这是get提交方式，没有body内容";
        }
        request = builder.build();
        FLog.dTag(TAG_HTTP, "\nURL:%s\nMethod:%s\nheader:[\n%s]\nbody:%s",
                request.url().toString(), request.method().toString(), request.headers().toString(), bodyLog);
        return request;
    }


    /**
     * builder map 格式的文件数据请求体：Request
     * <br/>内涵上传进度
     * <br/>V2
     *
     * @param url      请求地址
     * @param file     上传文件
     * @param params   header参数
     * @param callback 请求回调
     * @Description Request对象
     */
    public static Request builderFileMapRequest(String url, File file, Map<String, String> params, HttpProgressCallback callback) {
        Request request;
        String filePath = "";
        Request.Builder builder = new Request.Builder().url(url);
        if (null != params && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addHeader(key, params.get(key));
            }
        }
        if (null != file) {
            RequestBody body = builderFormFileData(file, callback);
            builder.post(body);
            filePath = file.getPath();
        }
        request = builder.build();
        FLog.dTag(TAG_HTTP, "\nURL:%s\nMethod:%s\nheader:[\n%s]\nbody:%s",
                request.url().toString(), request.method().toString(), request.headers().toString(), filePath);
        return request;
    }

    /*************************************************** do enqueue ***************************************/
    /**
     * Post数据请求
     * <br/>V2
     *
     * @param request  Request对象
     * @param callback 请求回调
     * @Description 异步请求
     */
    public static Call doEnqueue(final Request request, final HttpBaseCallback callback) {
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NetCode netCode = NetCode.UNKNOW;
                if (e instanceof UnknownHostException) {
                    netCode = NetCode.CODE_6904;
                } else if (e instanceof SocketTimeoutException) {
//                    if (null != e.getMessage()) {
//                        if (e.getMessage().contains("failed to connect to")) {
//                            //TODO 连接超时
//                        }
//                        if (e.getMessage().equals("timeout")) {
//                            //TODO 读写超时
//                        }
//                    }
                    netCode = NetCode.CODE_6905;
                }
                sendFailResultCallback(netCode.setInfo("\n\t\ttag:" + request.url().toString() + "\n\t\terr->" + e.getMessage()), callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response) {
                    sendFailResultCallback(NetCode.CODE_6010.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response"), callback);
                    return;
                }
                if (null == response.body()) {
                    sendFailResultCallback(NetCode.CODE_6020.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response.body()"), callback);
                    return;
                }
                String result = response.body().string();
                if (TextUtils.isEmpty(result)) {
                    sendFailResultCallback(NetCode.CODE_6021.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->body is empty"), callback);
                    return;
                }
                try {
                    if (response.isSuccessful()) {
                        sendSuccessResultCallback(request.url().toString(), result, callback);
                    } else {
                        sendFailResultCallback(NetCode.CODE_6011.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\tcode:" + response.code() + ",\n\t\terr->:" + response.message()), callback);
                    }
                } catch (Exception e) {
                    sendFailResultCallback(NetCode.CODE_6900.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->" + e.getMessage()), callback);
                }
            }
        });
        return call;
    }

    /**--------------------    异步文件下载    --------------------**/

    /**
     * 文件下载
     * <br/>V2
     *
     * @param request      Request对象
     * @param destFileDir  目标文件存储的文件目录
     * @param destFileName 目标文件存储的文件名
     * @param callback     请求回调
     * @Description 异步下载请求
     */
    public static Call doDownloadEnqueue(final Request request, final String destFileDir, final String destFileName, final HttpProgressCallback callback) {
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NetCode netCode = NetCode.UNKNOW;
                if (e instanceof UnknownHostException) {
                    netCode = NetCode.CODE_6904;
                } else if (e instanceof SocketTimeoutException) {
//                    if (null != e.getMessage()) {
//                        if (e.getMessage().contains("failed to connect to")) {
//                            //TODO 连接超时
//                        }
//                        if (e.getMessage().equals("timeout")) {
//                            //TODO 读写超时
//                        }
//                    }
                    netCode = NetCode.CODE_6905;
                }
                sendFailResultCallback(netCode.setInfo("\n\t\ttag:" + request.url().toString() + "\n\t\terr->" + e.getMessage()), callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (null == response) {
                    sendFailResultCallback(NetCode.CODE_6010.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response"), callback);
                    return;
                }
                if (null == response.body()) {
                    sendFailResultCallback(NetCode.CODE_6020.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->null == response.body()"), callback);
                    return;
                }
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    byte[] buffer = new byte[2048];
                    int len;
                    long currentTotalLen = 0L;
                    long totalLen = response.body().contentLength();

                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, destFileName);
                    if (file.exists()) {
                        //如果文件存在则删除
                        file.delete();
                    }
                    fileOutputStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        currentTotalLen += len;
                        sendProgressResultCallback(currentTotalLen, totalLen, callback, false);
                    }
                    fileOutputStream.flush();
                    sendProgressResultCallback(currentTotalLen, totalLen, callback, true);
                } catch (IOException e) {
                    if (e instanceof SocketException) {
                        sendFailResultCallback(NetCode.CODE_6901.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->" + e.getMessage()), callback);
                    } else {
                        sendFailResultCallback(NetCode.CODE_6903.setInfo("\n\t\ttag:" + call.request().url().toString() + "\n\t\terr->" + e.getMessage()), callback);
                    }
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return call;
    }

    /**
     * body log
     *
     * @param params
     * @return
     */
    private static String buildBodyLog4Map(Map<String, String> params) {
        String log = "";
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                log += key + ":" + value + ",";
            }
        }
        return log;
    }
}
