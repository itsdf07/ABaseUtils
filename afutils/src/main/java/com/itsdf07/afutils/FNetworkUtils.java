package com.itsdf07.afutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.itsdf07.afutils.log.FLog;

/**
 * @Description 网络相关工具类:
 * {@link android.Manifest.permission #INTERNET}
 * {@link android.Manifest.permission #ACCESS_NETWORK_STATE}
 * 1、网络是否可用:isNetworkAvailable
 * 2、WIFI网络是否可用:isWifiConnected
 * 3、MOBILE网络是否可用:isMobileConnected
 * 4、WiFi是否打开:isWiFiActive
 * 5、当前网络类型:getNetworkType
 * @Author itsdf07
 * @Time 2019/4/1/001
 */

public class FNetworkUtils {
    private static final String TAG = "FNetworkUtils";
    public static final int NETWORK_INVALID = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(@NonNull Context context) {
        if (null == context) {
            FLog.eTag(TAG, "异常:检查网络是否可用时，传入参数context为null");
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            FLog.eTag(TAG, "异常:检查网络是否可用时，ConnectivityManager为null");
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (null == networkinfo) {
            FLog.eTag(TAG, "异常:检查网络是否可用时，NetworkInfo为null");
            return false;
        }
        return networkinfo.isAvailable();
    }


    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return      
     */
    public static boolean isWifiConnected(@NonNull Context context) {
        if (null == context) {
            FLog.eTag(TAG, "异常:检查WIFI网络是否可用时，传入参数context为null");
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            FLog.eTag(TAG, "异常:检查WIFI网络是否可用时，ConnectivityManager为null");
            return false;
        }
        NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null == wifiNetworkInfo) {
            FLog.eTag(TAG, "异常:检查WIFI网络是否可用时，NetworkInfo为null");
            return false;
        }
        return wifiNetworkInfo.isAvailable();
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return      
     */
    public static boolean isMobileConnected(@NonNull Context context) {
        if (null == context) {
            FLog.eTag(TAG, "异常:检查MOBILE网络是否可用时，传入参数context为null");
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            FLog.eTag(TAG, "异常:检查MOBILE网络是否可用时，ConnectivityManager为null");
            return false;
        }
        NetworkInfo mobileNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null == mobileNetworkInfo) {
            FLog.eTag(TAG, "异常:检查WIFI网络是否可用时，NetworkInfo为null");
            return false;
        }
        return mobileNetworkInfo.isAvailable();
    }

    /**
     * 判断WiFi是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWiFiActive(@NonNull Context context) {
        if (null == context) {
            FLog.eTag(TAG, "异常:检查WiFi是否打开时，传入参数context为null");
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            FLog.eTag(TAG, "异常:检查WiFi是否打开时，ConnectivityManager为null");
            return false;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (null == networkInfo) {
            FLog.eTag(TAG, "异常:检查WiFi是否打开时，NetworkInfo为null");
            return false;
        }
        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取当前网络类型
     * <p>
     * GPRS     2G(2.5) General Packet Radia Service 114kbps
     * EDGE     2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
     * IDEN     2G Integrated Dispatch Enhanced Networks 集成数字增强型网络
     * CDMA     2G 电信 Code Division Multiple Access 码分多址
     * 1xRTT    2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
     * EVDO_B   3G EV-DO Rev.B 14.7Mbps 下行 3.5G
     * EHRPD    3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
     * HSPA     3G (分HSDPA,HSUPA) High Speed Packet Access 
     * HSPAP    3G HSPAP 
     * UMTS     3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
     * EVDO_0   3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
     * EVDO_A   3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
     * HSDPA    3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps 
     * HSUPA    3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
     * LTE      4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
     *
     * @param context
     * @return
     */
    public static int getNetworkType(@NonNull Context context) {
        if (null == context) {
            FLog.eTag(TAG, "异常:获取网络类型时，传入参数context为null");
            return NETWORK_INVALID;
        }
        ConnectivityManager manager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            FLog.eTag(TAG, "异常:获取网络类型时，ConnectivityManager为null");
            return NETWORK_INVALID;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (null == networkInfo) {
            FLog.eTag(TAG, "异常:获取网络类型时，NetworkInfo为null");
            return NETWORK_INVALID;
        }
        int stateCode = NETWORK_INVALID;
        if (networkInfo.isConnectedOrConnecting()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NETWORK_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NETWORK_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NETWORK_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE://4G 网络
                            stateCode = NETWORK_4G;
                            break;
                        default:
                            stateCode = NETWORK_INVALID;//未知网络 或者无网络连接
                    }
                    break;
                default:
                    stateCode = NETWORK_INVALID;
            }
        }
        return stateCode;
    }
}
