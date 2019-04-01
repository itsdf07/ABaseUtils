package com.itsdf07.afutils.log;

/**
 * @Description ：FLog 是基于{@link android.util.Log}的封装，但是使用更简单，信息查看更强大
 * FLog.init()
 * .setLogLevel(FLogLevel.FULL) //是否打印log
 * .setTag("itsdf07") //自定义tag
 * .setDefineLogFilePath("xxx/xxx/xxx.log") //自定义log存储路径
 * .setLog2Local(true) //设置是否本地存储log记录
 * .setShowThreadInfo(true)//是否显示线程信息
 * .setMethodCount(2) //显示函数栈中的方法数
 * .setMethodOffset(0); //控制从函数栈中的具体位置显示MethodCount数量的函数
 * @Author itsdf07
 * @Time 2019/4/1/001
 */

public class FLog {
    /**
     * 详细 - 显示所有日志消息（默认值）
     */
    public static final int VERBOSE = 2;
    /**
     * 调试 - 显示仅在开发期间有用的调试日志消息，以及此列表中的消息级别较低
     */
    public static final int DEBUG = 3;
    /**
     * 信息 - 显示常规使用的预期日志消息以及此列表中的消息级别
     */
    public static final int INFO = 4;
    /**
     * 警告 - 显示尚未出现错误的可能问题，以及此列表中的消息级别
     */
    public static final int WARN = 5;
    /**
     * 错误 - 显示导致错误的问题，以及此列表中的消息级别较低
     */
    public static final int ERROR = 6;
    /**
     * 断言 - 显示开发人员期望永远不会发生的问题
     */
    public static final int ASSERT = 7;

    /**
     * FLog内容打印实现类
     */
    private static FLoggerImpl mLogger = new FLoggerImpl();

    /**
     * 初始化FLog相关：
     */
    public static FLogSettings init() {
        if (mLogger == null) {
            mLogger = new FLoggerImpl();
        }
        return mLogger.getFLogSettings();
    }

    /**
     * 当前是否打印Log
     *
     * @return
     */
    public static boolean isLog() {
        return init().getLogLevel() == FLogLevel.FULL ? true : false;
    }


    /**
     * FLog.v("isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void v(String message, Object... args) {
        vTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    /**
     * FLog.v("dfsu", "isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param tag     tag
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void vTag(String tag, String message, Object... args) {
        mLogger.v(tag, message, args);
    }

    /**
     * FLog.d("isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void d(String message, Object... args) {
        dTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    /**
     * FLog.d("dfsu", "isFIleExist = %s, innerBasePath = %s", isFileExist, innerBasePath);
     *
     * @param tag     tag
     * @param message 要打印的内容
     * @param args    打印信息中的动态数据
     */
    public static void dTag(String tag, String message, Object... args) {
        mLogger.d(tag, message, args);
    }

    public static void i(String message, Object... args) {
        iTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    public static void iTag(String tag, String message, Object... args) {
        mLogger.i(tag, message, args);
    }

    public static void w(String message, Object... args) {
        wTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    public static void wTag(String tag, String message, Object... args) {
        mLogger.w(tag, message, args);
    }

    public static void e(String message, Object... args) {
        eTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    public static void eTag(String tag, String message, Object... args) {
        eTag(tag, null, message, args);
    }

    public static void eTag(String tag, Throwable throwable, String message, Object... args) {
        mLogger.e(tag, throwable, message, args);
    }

    public static void wtf(String message, Object... args) {
        wtfTag(mLogger.getFLogSettings().getTag(), message, args);
    }

    public static void wtfTag(String tag, String message, Object... args) {
        wtfTag(tag, null, message, args);
    }

    public static void wtfTag(String tag, Throwable throwable, String message, Object... args) {
        mLogger.wtf(tag, throwable, message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        mLogger.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        mLogger.xml(xml);
    }
}
