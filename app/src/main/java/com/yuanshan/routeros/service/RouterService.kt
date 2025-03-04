package com.yuanshan.routeros.service

import com.yuanshan.routeros.MyApplication
import com.yuanshan.routeros.config.RouterConfig
import com.yuanshan.routeros.data.FirewallRule
import com.yuanshan.routeros.data.InterfaceInfo
import com.yuanshan.routeros.data.RouterStatus
import com.yuanshan.routeros.utils.CommonUtil.formatDouble
import com.yuanshan.routeros.utils.CommonUtil.processRuntimeString
import com.yuanshan.routeros.utils.CommonUtil.processVersionString
import com.yuanshan.routeros.utils.TimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.legrange.mikrotik.ApiConnection
import javax.net.SocketFactory

class RouterService {
    private var connection: ApiConnection? = null

    // 新增的流量历史数据存储
    private val lastRxBytes = mutableMapOf<String, Long>()
    private val lastTxBytes = mutableMapOf<String, Long>()
    private val lastTime = mutableMapOf<String, Long>()
    private val trafficHistory = mutableMapOf<String, MutableList<TrafficEntry>>()

    // 新增的常量
    private val maxHistoryPoints = 30 // 保存60个数据点

    // 新增数据类
    data class TrafficEntry(
        val interval: Long,
        val rxRate: Double,
        val txRate: Double
    )

    // 在 RouterService 类中添加
    companion object {
        // 全局基准时间
        var globalBaseTime: Long = TimeUtil.getCurrentTimestampRoundedToSecond()
            private set
    }


    // 创建连接
    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            val context = MyApplication.instance // 需要创建自定义 Application 类
            val config = RouterConfig.getConfig(context)
            connection = ApiConnection.connect(
                SocketFactory.getDefault(),
                config.host,
                config.port.toInt(),
                60
            ).apply {
                login(config.username, config.password)
            }
        } catch (e: Exception) {
            throw Exception("连接失败: ${e.message}")
        }
    }

    // 断开连接
    fun disconnect() {
        connection?.close()
        connection = null
    }

    // 获取系统状态
    suspend fun getSystemStatus(): RouterStatus = withContext(Dispatchers.IO) {
        connection?.let { conn ->
            val result = conn.execute("/system/resource/print")
            if (result.isNotEmpty()) {
                val map = result[0]
                RouterStatus(
                    cpuLoad = map["cpu-load"] ?: "",
                    memoryTotal = map["total-memory"] ?: "",
                    memoryFree = map["free-memory"] ?: "",
                    uptime = processRuntimeString(map["uptime"] ?: ""),
                    version = processVersionString(map["version"] ?: "")
                )
            } else {
                throw Exception("获取系统状态失败")
            }
        } ?: throw Exception("未连接到路由器")
    }

    // 获取接口流量数据
    suspend fun getInterfaces(): List<InterfaceInfo> = withContext(Dispatchers.IO) {
        connection?.let { conn ->
            val result = conn.execute("/interface/print")

            result
                .filter { map -> map["type"] == "pppoe-out" || map["type"] == "wg" || map["type"] == "bridge" }
                .map { map ->
                    val name = map["name"] ?: ""
                    val currentRxBytes = (map["rx-byte"] ?: "0").toLong()
                    val currentTxBytes = (map["tx-byte"] ?: "0").toLong()

                    // 计算流量速率
                    val currentTime = System.currentTimeMillis();
                    val (txRate, rxRate) = calculateTrafficInTimeRange(
                        lastTime[name] ?: currentTime,
                        currentTime,
                        lastRxBytes[name] ?: 0,
                        lastTxBytes[name] ?: 0,
                        currentRxBytes,
                        currentTxBytes
                    );

                    // 更新历史数据
                    updateTrafficHistory(
                        name,
                        lastTime[name] ?: currentTime,
                        currentTime,
                        formatDouble(rxRate),
                        formatDouble(txRate)
                    )

                    // 更新上次数据
                    lastRxBytes[name] = currentRxBytes
                    lastTxBytes[name] = currentTxBytes
                    lastTime[name] = currentTime

                    InterfaceInfo(
                        name = map["name"] ?: "",
                        type = map["type"] ?: "",
                        status = map["running"] ?: "false",
                    )
                }
        } ?: emptyList()
    }

    // 获取防火墙规则信息
    suspend fun getFirewallRules(): List<FirewallRule> = withContext(Dispatchers.IO) {
        connection?.let { conn ->
            try {
                val result = conn.execute("/ip/firewall/filter/print")

                result.mapNotNull { item ->
                    if (item["chain"] == "input") {
                        com.yuanshan.routeros.data.FirewallRule(
                            id = item[".id"] ?: "",
                            chain = item["chain"] ?: "",
                            action = item["action"] ?: "",
                            bytes = item["bytes"] ?: "0",
                            packets = item["packets"] ?: "0",
                            comment = item["comment"],
                            disabled = item["disabled"] == "true"
                        )
                    } else null
                }
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    // 计算时间范围流量速率
    fun calculateTrafficInTimeRange(
        startTime: Long,
        endTime: Long,
        lastRxBytes: Long,
        lastTxBytes: Long,
        currentRxBytes: Long,
        currentTxBytes: Long
    ): Pair<Double, Double> {
        val rxRate = (currentRxBytes - lastRxBytes) / ((endTime - startTime) / 1000.0) //计算byte/s
        val txRate = (currentTxBytes - lastTxBytes) / ((endTime - startTime) / 1000.0) //计算byte/s
        return Pair(rxRate, txRate);
    }

    // 更新历史数据(bytes)
    private fun updateTrafficHistory(
        interfaceName: String,
        lastTime: Long,
        currentTime: Long,
        rxRate: Double,
        txRate: Double,
    ) {
        val history = trafficHistory.getOrPut(interfaceName) { mutableListOf() }
        // 确保时间戳不重复
        val lastEntry = history.lastOrNull()

        // 添加新的数据点
        if (lastEntry == null) {
            history.add(TrafficEntry(0, 0.0, 0.0)) // 添加新的数据点
        } else {
            val lastInterval = lastEntry.interval
            val timeDiff = (currentTime - lastTime) / 1000
            val currentInterval = try {
                Math.addExact(lastInterval, timeDiff)
            } catch (e: ArithmeticException) {
                // 检测到溢出，重置基准时间和间隔
                globalBaseTime =  TimeUtil.getCurrentTimestampRoundedToSecond()// 更新为当前时间的整秒
                history.clear() // 清空历史数据
                0L // 重置间隔为0
            }
            history.add(
                TrafficEntry(
                    currentInterval,
                    rxRate / 1000,
                    txRate / 1000
                )
            ) //加新的数据点

            // 保持最大数据点数量
            while (history.size > maxHistoryPoints) {
                history.removeAt(0)
            }
        }
    }

    // 新增获取历史数据的方法
    fun getTrafficHistory(interfaceName: String): List<TrafficEntry> {
        return trafficHistory[interfaceName] ?: emptyList()
    }
}