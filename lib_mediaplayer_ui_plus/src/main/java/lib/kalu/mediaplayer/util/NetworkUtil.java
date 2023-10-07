package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public final class NetworkUtil {

    public static final int NO_NETWORK = 0;
    public static final int NETWORK_CLOSED = 1;
    public static final int NETWORK_ETHERNET = 2;
    public static final int NETWORK_WIFI = 3;
    public static final int NETWORK_MOBILE = 4;
    public static final int NETWORK_UNKNOWN = -1;

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMobileConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isWifiConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnected(Context context, int type) {
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null == connectMgr)
                throw new Exception();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                for (NetworkInfo info : connectMgr.getAllNetworkInfo()) {
                    if (info.getType() == type) {
                        return info.isAvailable();
                    }
                }
            } else {
                for (Network network : connectMgr.getAllNetworks()) {
                    NetworkInfo networkInfo = connectMgr.getNetworkInfo(network);
                    if (networkInfo.getType() == type) {
                        return networkInfo.isAvailable();
                    }
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }

    public static int getNetworkType(Context context) {
        //改为context.getApplicationContext()，防止在Android 6.0上发生内存泄漏
        ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr == null) {
            return NO_NETWORK;
        }
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            // 没有任何网络
            return NO_NETWORK;
        }
        if (!networkInfo.isConnected()) {
            // 网络断开或关闭
            return NETWORK_CLOSED;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            // 以太网网络
            return NETWORK_ETHERNET;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
            return NETWORK_WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
            switch (networkInfo.getSubtype()) {
                // 2G
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    // 3G
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    // 4G
                case TelephonyManager.NETWORK_TYPE_LTE:
                    // 5G
                    return NETWORK_MOBILE;
            }
        }
        // 未知网络
        return NETWORK_UNKNOWN;
    }
}
