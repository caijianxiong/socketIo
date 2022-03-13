package com.example.socketdemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchDevicesTest {
    public static void main(String[] args) {
        System.out.println("start");
        searchWifiDevices();
//        System.out.println("本端WiFi IP：" + getLocalWifiIP());

    }

    /**
     * 获取本段WiFi 局域网ip
     */
    public static String getLocalWifiIP() {
        //匹配C类地址的IP
        String regexCIp = "^192\\.168\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";
        //匹配A类地址
        String regexAIp = "^10\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";
        //匹配B类地址
        final String regexBIp = "^172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";

        String hostIp;
        Pattern ip = Pattern.compile("(" + regexAIp + ")|" + "(" + regexBIp + ")|" + "(" + regexCIp + ")");
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress address;
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                address = inetAddresses.nextElement();
                String hostAddress = address.getHostAddress();
                Matcher matcher = ip.matcher(hostAddress);
                if (matcher.matches()) {
                    hostIp = hostAddress;
                    return hostIp;
                }

            }
        }
        return "";

    }


    /**
     * 查找局域网内设备及IP ----查找速度慢，不准确
     */
    private static void searchWifiDevices() {
        try {
            //设置IP地址网段
            String ips = "192.168.10.";
            String ip;
            InetAddress addip;
            //遍历IP地址
            for (int i = 200; i < 255; i++) {
                ip = ips + i;
                addip = InetAddress.getByName(ip);
                //获取登录过的设备
                if (!ip.equals(addip.getHostName())) {
                    //检查设备是否在线，其中1000ms指定的是超时时间
                    InetAddress finalAddip = addip;
                    String finalIp = ip;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean status = false;     // 当返回值是true时，说明host是可用的，false则不可。
                            try {
                                status = InetAddress.getByName(finalAddip.getHostName()).isReachable(200);
                                System.out.println("IP地址为:" + finalIp + "\t\t设备名称为: " + finalAddip.getHostName() + "\t\t是否可用: " + (status ? "可用" : "不可用"));
                            } catch (IOException e) {
                                System.err.println("Unable to find: " + e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    System.out.print(" " + i);
                }
            }
        } catch (java.io.IOException uhe) {
            System.err.println("Unable to find: " + uhe.getLocalizedMessage());
        }
    }
}
