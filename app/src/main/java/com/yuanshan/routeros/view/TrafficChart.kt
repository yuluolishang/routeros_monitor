package com.yuanshan.routeros.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.yuanshan.routeros.data.InterfaceInfo
import com.yuanshan.routeros.service.RouterService
import com.yuanshan.routeros.utils.TimeUtil
import kotlinx.coroutines.delay
import kotlin.math.abs


@Composable
fun TrafficChart(
    interfaceData: Map<String, List<Entry>>,
    interfaces: List<InterfaceInfo>,
    isUpload: Boolean,
    modifier: Modifier = Modifier
) {
    // 在 Composable 作用域内获取所有需要的颜色
    val chartTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f).toArgb()
    val chartGridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f).toArgb()

    // 使用更有区分度的自定义颜色集
    val distinctColors = listOf(
        Color(0xFF2196F3).toArgb(),  // 蓝色
        Color(0xFFFF5722).toArgb(),  // 橙色
        Color(0xFF4CAF50).toArgb(),  // 绿色
        Color(0xFFE91E63).toArgb(),  // 粉红色
        Color(0xFF9C27B0).toArgb(),  // 紫色
        Color(0xFFFFEB3B).toArgb(),  // 黄色
        Color(0xFF795548).toArgb(),  // 棕色
        Color(0xFF607D8B).toArgb(),  // 蓝灰色
    )

    // 使用remember确保在组合期间保持一致的baseTime
    val baseTime = remember { mutableStateOf(RouterService.globalBaseTime) } // 使用全局变量

    // 使用 DisposableEffect 或 LaunchedEffect 来监听变化
    LaunchedEffect(Unit) {
        // 定期检查并更新 baseTime
        while(true) {
            delay(1000)
            baseTime.value = RouterService.globalBaseTime
        }
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // 基本配置保
                description.isEnabled = false
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                // X轴配置
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = chartTextColor
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return TimeUtil.getTimeAfterSecondsFromBase(baseTime.value, value.toInt())
                        }
                    }
                    // 限制X轴标签数量
                    isGranularityEnabled = true     // 启用粒度控制
                    granularity = 20f               // 标签之间最小间隔为20秒
                    labelCount = 3                 // 限制标签数量为5个
                    setAvoidFirstLastClipping(false) // 避免首尾标签被裁剪
                }

                // Y轴配置
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = chartGridColor  // 网格线颜色
                    textColor = chartTextColor
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            if (value >= 1024) {
                                return "${abs(value / 1024).toInt()} MB"
                            } else {
                                return "${abs(value).toInt()} KB"
                            }
                        }
                    }
                    // 设置 Y 轴标签数量，使刻度更清晰
                    labelCount = 4
                    setDrawZeroLine(true) // 强调零线
                }
                axisRight.isEnabled = false

                // 改进图例配置，将图例放在底部且水平排列
                legend.apply {
                    textColor = chartTextColor
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    orientation = Legend.LegendOrientation.HORIZONTAL  // 修改为水平方向
                    setDrawInside(false)
                    form = Legend.LegendForm.CIRCLE
                    formSize = 4f  // 减小图例符号大小
                }

                // 为图例在底部预留足够空间
                setExtraBottomOffset(10f)  // 增加底部空间
            }
        },
        modifier = modifier.fillMaxSize()
    ) { chart ->
        val dataSets = ArrayList<ILineDataSet>()

        interfaces.forEachIndexed { index, interface_ ->
            val colorIndex = index % distinctColors.size
            val interfaceEntries = interfaceData[interface_.name] ?: emptyList()

            val entries = if (isUpload) {
                interfaceEntries.filter { it.y >= 0 }
            } else {
                interfaceEntries.filter { it.y <= 0 }.map {
                    Entry(it.x, abs(it.y))
                }
            }

            if (entries.isNotEmpty()) {
                LineDataSet(entries, interface_.name).apply {
                    color = distinctColors[colorIndex]
                    lineWidth = 2f
                    setDrawCircles(false)
                    // 关闭数值显示
                    setDrawValues(false)
                    // 添加线条填充效果，增强视觉区分度
                    setDrawFilled(true)
                    fillAlpha = 50  // 半透明填充
                    fillColor = distinctColors[colorIndex]
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    dataSets.add(this)
                }
            }
        }

        chart.data = LineData(dataSets)
        chart.notifyDataSetChanged()
        chart.invalidate()
    }
}