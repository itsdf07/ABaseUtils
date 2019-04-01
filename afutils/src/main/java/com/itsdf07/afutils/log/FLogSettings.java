package com.itsdf07.afutils.log;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 用于确定日志设置，如方法计数、线程信息可见性等设置
 * @Author itsdf07
 * @Time 2019/4/1/001
 */

public class FLogSettings {

    /**
     * 日期格式
     */
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd");
    /**
     * FLog的根路径
     */
    private final String logRoot = "FLOG";
    /**
     * Log的TAG
     */
    private String TAG = "itsdf07";

    /**
     * 是否保存Log信息
     */
    private boolean isLog2Local = false;

    /**
     * 默认Log本地文件存储路径
     * Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
     */
    private final String defaultLogFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    /**
     * 自定义Log本地存储路径：需指向写入文件，如:xxx/xxx/xxx.log
     */
    private String defineLogFilePath = "";

    /**
     * 是否打印线程名称
     */
    private boolean isShowThreadInfo = false;

    /**
     * Log打印开关
     */
    private FLogLevel logLevel = FLogLevel.FULL;

    /**
     * 设置Log信息中打印的函数栈中的函数计数
     */
    private int methodCount = 2;
    /**
     * 设置Log信息中打印函数栈的起始位置，即用于控制需要打印哪几个(methodCount)函数
     */
    private int methodOffset = 0;

    private FLogAdapterImpl logAdapter;

    public String getLogRoot() {
        return logRoot;
    }

    public String getTag() {
        return TAG;
    }

    /**
     * 设置Log打印时的Tag
     *
     * @param tag
     * @return
     */
    public FLogSettings setTag(String tag) {
        if (null == tag) {
            throw new NullPointerException("tag may not be null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        }
        this.TAG = tag;
        return this;
    }

    /**
     * 是否本地保存Log信息
     *
     * @return
     */
    public boolean isLog2Local() {
        return isLog2Local;
    }

    /**
     * 设置是否本地保存Log信息
     *
     * @param isLog2Local
     */
    public FLogSettings setLog2Local(boolean isLog2Local) {
        this.isLog2Local = isLog2Local;
        return this;
    }

    /**
     * 默认Log路径Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FLOG" +  File.separator
     *
     * @return
     */
    public String getDefaultLogFilePath() {
        Date now = new Date();
        String date = mSimpleDateFormat.format(now);
        return defaultLogFilePath + getLogRoot() + File.separator + date + ".log";
    }

    public String getDefineLogFilePath() {
        return defineLogFilePath;
    }

    /**
     * 自定义Log本地存储路径：需指向写入文件
     *
     * @param defineLogFilePath xxx/xxx/xxx.log
     */
    public FLogSettings setDefineLogFilePath(String defineLogFilePath) {
        this.defineLogFilePath = defineLogFilePath;
        return this;
    }

    /**
     * 是否显示线程名称：
     *
     * @return false：不显示线程信息，即只打印内容
     */
    public boolean isShowThreadInfo() {
        return isShowThreadInfo;
    }

    /**
     * 不打印线程信息
     *
     * @return
     */
    public FLogSettings hideThreadInfo() {
        isShowThreadInfo = false;
        return this;
    }

    /**
     * 设置是否显示线程信息
     *
     * @param isShowThreadInfo
     * @return
     */
    public FLogSettings setShowThreadInfo(boolean isShowThreadInfo) {
        this.isShowThreadInfo = isShowThreadInfo;
        return this;
    }

    public FLogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * 设置是否打印Log信息
     *
     * @param logLevel
     * @return
     * @see FLogLevel
     */
    public FLogSettings setLogLevel(FLogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    /**
     * 设置Log信息中打印的函数栈中的函数计数
     *
     * @param methodCount
     */
    public FLogSettings setMethodCount(int methodCount) {
        this.methodCount = methodCount;
        return this;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    /**
     * 设置Log信息中打印函数栈的起始位置，即用于控制需要打印哪几个(methodCount)函数
     *
     * @param methodOffset
     */
    public FLogSettings setMethodOffset(int methodOffset) {
        this.methodOffset = methodOffset;
        return this;
    }

    public FLogSettings setLogAdapter(FLogAdapterImpl adapter) {
        this.logAdapter = adapter;
        return this;
    }

    public FLogAdapterImpl getLogAdapter() {
        if (logAdapter == null) {
            logAdapter = new FLogAdapterImpl();
        }
        return logAdapter;
    }
}
