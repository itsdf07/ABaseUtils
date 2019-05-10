package com.itsdf07.afutils.okhttp3.callback;

/**
 * @Description ：Http请求过程中的回调
 * @Author itsdf07
 * @Time 2018/09/13
 */
public interface OkHttp3Callback {
    /**
     * @param result   请求结果
     * @param isDecode 是否加密
     * @Description http成功响应
     */
    void onSuccess(String result, boolean isDecode);

    /**
     * @param code 错误码
     * @param msg  失败信息
     * @Description http响应失败
     */
    void onFailure(String code, String msg);
}
