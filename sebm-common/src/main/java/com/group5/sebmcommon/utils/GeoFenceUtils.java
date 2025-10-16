package com.group5.sebmcommon.utils;

public class GeoFenceUtils {

    // 地球半径，单位：米
    private static final double EARTH_RADIUS = 6371000;

    // 将角度转换为弧度
    private static double rad(double degree) {
        return degree * Math.PI / 180.0;
    }

    // 计算两点间距离（Haversine公式）
    public static double distance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = rad(lat2 - lat1);
        double dLon = rad(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(rad(lat1)) * Math.cos(rad(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c; // 返回米
    }

    // 判断点是否在电子围栏内
    public static boolean isInGeofence(String deviceLon, String deviceLat,
                                       double centerLon, double centerLat, double radius) {
        double deviceLonDouble = Double.parseDouble(deviceLon);
        double deviceLatDouble = Double.parseDouble(deviceLat);
        double dist = distance(deviceLonDouble, deviceLatDouble, centerLon, centerLat);
        return dist <= radius;
    }
}
