package com.itsdf07.afutils.okhttp3.callback;

/**
 * @Description
 * @Author itsdf07
 * @Time 2018/9/28/28
 */

public interface HttpProgressCallback extends HttpBaseCallback {
    /**
     * 下载/上传进度
     *
     * @param currentLen 已经上传的字节大小
     * @param totalLen   文件的总字节大小
     */
    void onProgress(long currentLen, long totalLen);
}