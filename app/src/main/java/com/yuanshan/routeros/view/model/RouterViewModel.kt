package com.yuanshan.routeros.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.yuanshan.routeros.data.InterfaceInfo
import com.yuanshan.routeros.data.RouterStatus
import com.yuanshan.routeros.service.RouterService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouterViewModel : ViewModel() {
    private val routerService = RouterService()
    private val _routerState = MutableStateFlow(RouterState())
    val routerState = _routerState.asStateFlow()
    private var updateJob: Job? = null

    // 新增流量历史数据的 Flow
    private val _trafficHistory = MutableStateFlow<Map<String, List<Entry>>>(emptyMap())
    val trafficHistory = _trafficHistory.asStateFlow()

    // 防火墙规则状态流
    private val _firewallRules = MutableStateFlow<List<com.yuanshan.routeros.data.FirewallRule>>(emptyList())
    val firewallRules = _firewallRules.asStateFlow()

    // 初始化连接
    init {
        connect()
    }

    // 连接逻辑
    private fun connect() {
        viewModelScope.launch {
            _routerState.value = _routerState.value.copy(isLoading = true)
            try {
                routerService.connect()
                startPeriodicUpdate()
            } catch (e: Exception) {
                _routerState.value = _routerState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    // 重连逻辑
    fun reconnect() {
        viewModelScope.launch {
            _routerState.value = _routerState.value.copy(isLoading = true)
            try {
                updateJob?.cancel()
                routerService.disconnect()
                routerService.connect()
                startPeriodicUpdate()
            } catch (e: Exception) {
                _routerState.value = _routerState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    // 清理资源
    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
        routerService.disconnect()
    }

    // 定时更新数据
    private fun startPeriodicUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                try {
                    updateRouterData()
                } catch (e: Exception) {
                    _routerState.value = _routerState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
                delay(2000) // 1秒间隔
            }
        }
    }

    // 更新数据逻辑
    private suspend fun updateRouterData() {
        try {
            val status = routerService.getSystemStatus()
            val interfaces = routerService.getInterfaces()
            val firewallRules = routerService.getFirewallRules()

            // 更新接口状态
            _routerState.value = RouterState(
                status = status,
                interfaces = interfaces,
                isLoading = false
            )

            // 更新防火墙规则
            _firewallRules.value = firewallRules

            // 更新流量历史数据
            val trafficData = interfaces.associate { interface_ ->
                val history = routerService.getTrafficHistory(interface_.name)
                val entries = history.flatMap { entry ->
                    listOf(
                        Entry(entry.interval.toFloat(), entry.txRate.toFloat()),     // tx为正值
                        Entry(entry.interval.toFloat(), (-entry.rxRate).toFloat())      // rx为负值
                    )
                }.sortedBy { it.x }  // 按时间排序
                interface_.name to entries
            }
            _trafficHistory.value = trafficData

        } catch (e: Exception) {
            _routerState.value = _routerState.value.copy(
                error = e.message,
                isLoading = false
            )
        }
    }
}

// RouterState 数据类
data class RouterState(
    val status: RouterStatus = RouterStatus(),
    val interfaces: List<InterfaceInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)