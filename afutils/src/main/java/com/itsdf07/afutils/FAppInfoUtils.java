package com.itsdf07.afutils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.itsdf07.afutils.bean.FAppInfo;
import com.itsdf07.afutils.log.FLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description App相关信息获取的工具类
 * @Author itsdf07
 * @Time 2019/5/9/009
 */

public class FAppInfoUtils {

    /**
     * 获取app的 versionCode
     *
     * @param context
     * @return 如 1
     */
    public static int getVersionCode(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return -1;
        }

        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return -1;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null == packageInfo) {
                FLog.wtf("PackageInfo is null");
                return -1;
            }
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            FLog.e(e.toString());
        }
        return -1;
    }

    /**
     * 获取app的 versionName
     *
     * @param context
     * @return 如 1.0.0
     */
    public static String getVersionName(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return "";
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null == packageInfo) {
                FLog.wtf("PackageInfo is null");
                return "";
            }
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            FLog.e(e.toString());
        }
        return "";
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return "";
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null == packageInfo) {
                FLog.wtf("PackageInfo is null");
                return "";
            }
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            FLog.e(e.toString());
        }
        return "";
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return "";
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null == packageInfo) {
                FLog.wtf("PackageInfo is null");
                return "";
            }
            return packageInfo.packageName;
        } catch (Exception e) {
            FLog.e(e.toString());
        }
        return "";
    }

    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static Bitmap getIconBitmap(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return null;
        }
        BitmapDrawable bd = (BitmapDrawable) getIconDrawable(context);
        if (null == bd) {
            return null;
        }
        return bd.getBitmap();
    }

    /**
     * 获取图标 Drawable
     *
     * @param context
     */
    public static Drawable getIconDrawable(Context context) {
        if (null == context) {
            FLog.e("Context not null");
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return null;
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            if (null == applicationInfo) {
                FLog.wtf("ApplicationInfo is null");
                return null;
            }
            Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
            return d;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }


    /**
     * 获取application中指定的meta-data。本例中，调用方法时key如：UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */

    public static String getAppMetaData(Context context, String key) {
        if (null == context) {
            FLog.e("Context not null");
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            FLog.wtf("PackageManager is null");
            return "";
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null == applicationInfo) {
                FLog.wtf("ApplicationInfo is null");
                return "";
            }

            if (null == applicationInfo.metaData) {
                FLog.e("metaData is null");
                return "";
            }
            return applicationInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            FLog.e(e.toString());
        }
        return "";
    }
//    使用getAppMetaData（context,"getAppMetaData"）

    /**
     * 获取已安装应用信息（不包含系统自带）
     *
     * @param context
     * @return 已安装应用列表，异常时返回 null
     */
    public static ArrayList<FAppInfo> getInstallApps(Context context) {
        ArrayList<FAppInfo> infos = new ArrayList<>();
        if (null == context) {
            FLog.e("Context not null");
            return infos;
        }
        List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : apps) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                FAppInfo appInfo = new FAppInfo();
                appInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setPackageName(packageInfo.packageName);
                infos.add(appInfo);
            }
        }
        return infos;
    }

    /**
     * 获取某个非系统预装的app信息
     *
     * @param context
     * @param packageName 对应的app包名
     * @return 查找到的安装应用信息，异常、无结果时返回 null
     */
    public static FAppInfo getInstallApp(Context context, String packageName) {
        FAppInfo appInfo = null;
        if (null == context) {
            FLog.e("Context not null");
            return appInfo;
        }
        List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : apps) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if (packageInfo.packageName.equals(packageName)) {
                    appInfo = new FAppInfo();
                    appInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
                    appInfo.setVersionName(packageInfo.versionName);
                    appInfo.setVersionCode(packageInfo.versionCode);
                    appInfo.setPackageName(packageInfo.packageName);
                    break;
                }
            }
        }
        return appInfo;
    }
}
