package com.itsdf07.afutils.okhttp3.callback;


import com.itsdf07.afutils.okhttp3.NetCode;

/**
 * @Description
 * @Author itsdf07
 * @Time 2018/9/28/028
 */

public interface HttpBaseCallback {
    /**
     * @param result 请求结果
     * @Description http成功响应
     */
    void onSuccess(String result);

    /**
     * @param netCode 错误码
     * @param msg     失败信息
     * @Description http响应失败
     */
    void onFailure(NetCode netCode, String msg);
}
