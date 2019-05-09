package com.itsdf07.afutils.bean;

/**
 * @Description
 * @Author itsdf07
 * @Time 2019/5/9/009
 */

public class FAppInfo {
    /**
     * App名称
     */
    private String appName = "";
    /**
     * App包名
     */
    private String packageName = "";
    /**
     * APP发布版本
     */
    private String versionName = "";
    /**
     * APP代码的相对版本
     */
    private int versionCode = 1;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "appName:" + appName + ",versionName:" + versionName + ",versionCode:" + versionCode + ",packageName:" + packageName;
    }
}
