package com.yuanshan.routeros.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuanshan.routeros.view.model.RouterViewModel

@Composable
fun RouterDashboard(viewModel: RouterViewModel) {
    val routerState by viewModel.routerState.collectAsState()
    val trafficHistory by viewModel.trafficHistory.collectAsState()
    val firewallRules by viewModel.firewallRules.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 左侧系统状态卡片列
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 第二行：运行时间和系统版本
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 运行时间卡片
                Box(modifier = Modifier.weight(1f)) {
                    InfoTag(title = "运行时间", content = routerState.status.uptime)
                }

                // 系统版本卡片
                Box(modifier = Modifier.weight(1f)) {
                    InfoTag(title = "系统版本", content = routerState.status.version)
                }
            }
            // 第一行：CPU和内存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CPU 状态卡片
                Box(modifier = Modifier.weight(1f)) {
                    val cpuValue = routerState.status.cpuLoad
                        .removeSuffix("%")
                        .toDoubleOrNull() ?: 0.0
                    CircularProgressCard(
                        progress = cpuValue / 100,
                        title = "CPU 负载"
                    )
                }

                // 内存状态卡片

                Box(modifier = Modifier.weight(1f)) {
                    val memoryFree = routerState.status.memoryFree.toDoubleOrNull() ?: 0.0
                    val memoryTotal = routerState.status.memoryTotal.toDoubleOrNull() ?: 1.0
                    var memoryUsage = memoryTotal - memoryFree
                    val memValue = if (memoryTotal > 0) memoryUsage / memoryTotal else 0.0
                    CircularProgressCard(
                        progress = memValue,
                        title = "内存使用"
                    )
                }

            }

            // 第一行：CPU和内存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 在适当位置添加防火墙表格
                FirewallTable(
                    rules = firewallRules,
                    modifier = Modifier.fillMaxWidth()
                )
            }


        }

        // 右侧接口列表
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {

            // 使用 weight 均分高度
            Box(modifier = Modifier.weight(1f)) {
                // 上传流量趋势图
                StatusCard(
                    title = "下载",
                    value = "",
                    modifier = Modifier.fillMaxSize()
                ) {
                    TrafficChart(
                        interfaceData = trafficHistory,
                        interfaces = routerState.interfaces,
                        isUpload = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            // 间隔
            Box(modifier = Modifier.padding(4.dp))

            // 下载流量趋势图
            Box(modifier = Modifier.weight(1f)) {
                StatusCard(
                    title = "上传",
                    value = "",
                    modifier = Modifier.fillMaxSize()
                ) {
                    TrafficChart(
                        interfaceData = trafficHistory,
                        interfaces = routerState.interfaces,
                        isUpload = false,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            if (routerState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            routerState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}