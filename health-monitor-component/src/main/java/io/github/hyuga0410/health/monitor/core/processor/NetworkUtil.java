package io.github.hyuga0410.health.monitor.core.processor;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * NetUtil
 * lo0:loopback回环地址，一般是127.0.0.1
 * 【loopback指本地环回接口（或地址），亦称回送地址()。此类接口是应用最为广泛的一种虚接口，几乎在每台路由器上都会使用。】
 * gif0: software Network Interface【软件网络接口】
 * stf0: 6to4 tunnel interface【ipv6->ipv4通道接口】
 * en0: Ethernet【以太网0 有线网卡】
 * p2po: Point-to-Point 协议
 * bridge0: 第2层桥接
 * utun0:networksetup -listallhardwareports
 *
 * @author hyuga
 * @since 2021-01-21 下午5:51
 */
@Slf4j
class NetworkUtil {

    public static void main(String[] args) {
        System.out.println(getLocalIpAddr());
    }

    /**
     * 获取本地IP地址【本地以太网IP】
     * loopback：回环网卡就是微软的一种类似于虚拟网卡的一种设备，它能够被安装在一个没有网卡（这里是硬件网卡），的环境下，或者用于测试多个宿主环境。
     *
     * @return string
     */
    public static String getLocalIpAddr() {
        String clientIp = null;
        Enumeration<NetworkInterface> networks;
        try {
            // 获取所有网卡设备
            networks = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            log.error("获取所有网卡设备失败:{}", e.getMessage());
            return null;
        }
        InetAddress ip;
        Enumeration<InetAddress> address;
        // 遍历网卡设备
        while (networks.hasMoreElements()) {
            NetworkInterface ni = networks.nextElement();
            try {
                // 过滤掉 不在线网卡、loopback回环设备、虚拟网卡
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
            } catch (SocketException e) {
                log.error("NetUtil.SocketException:{}", e.getMessage());
            }
            address = ni.getInetAddresses();
            // 遍历InetAddress信息
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                // 不是回环地址&是站点本地地址&主机地址不包含:
                if (!ip.isLoopbackAddress() && ip.isSiteLocalAddress() && !ip.getHostAddress().contains(":")) {
                    try {
                        clientIp = ip.toString().split("/")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        clientIp = null;
                    }
                }
            }
        }
        return clientIp;
    }

}
