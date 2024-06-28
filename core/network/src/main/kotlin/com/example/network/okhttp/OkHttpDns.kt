package com.example.network.okhttp

import android.content.Context
import com.alibaba.sdk.android.httpdns.HTTPDNSResult
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.RequestIpType
import com.alibaba.sdk.android.httpdns.log.HttpDnsLog
import com.blankj.utilcode.util.LogUtils
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

class OkHttpDns(private val context: Context) : Dns {

    override fun lookup(hostname: String): List<InetAddress> {
        val httpdnsResult: HTTPDNSResult = HttpDns.getService(context, "113753")
            .getHttpDnsResultForHostSync(hostname, RequestIpType.both)
        val inetAddresses: MutableList<InetAddress> = ArrayList()
        var address: InetAddress
        try {
            if (httpdnsResult.ips != null) {
                // 处理IPv4地址
                for (ipv4 in httpdnsResult.ips) {
                    address = InetAddress.getByName(ipv4)
                    inetAddresses.add(address)
                }
            }
            if (httpdnsResult.ipv6s != null) {
                // 处理IPv6地址
                for (ipv6 in httpdnsResult.ipv6s) {
                    address = InetAddress.getByName(ipv6)
                    inetAddresses.add(address)
                }
            }
        } catch (_: UnknownHostException) {
        }
        return inetAddresses.ifEmpty { Dns.SYSTEM.lookup(hostname) }
    }
}