package com.yuanshan.routeros.utils

object CommonUtil {

    // 格式化小数保留整数位,返回double
    fun formatDouble(value: Double): Double {
        return String.format("%.0f", value).toDouble()
    }


    /**
     * 处理运行时间字符串，只显示最长的时间单位
     */
    fun processRuntimeString(runtimeString: String): String {
        // 按优先级查找时间单位
        val timeUnits = listOf("d", "w", "h", "m", "s")

        for (unit in timeUnits) {
            val regex = "(\\d+)\\s*$unit".toRegex()
            val matchResult = regex.find(runtimeString)

            if (matchResult != null) {
                val value = matchResult.groupValues[1]
                return "$value$unit"
            }
        }

        // 如果没有找到任何时间单位，返回原始字符串
        return runtimeString
    }

    /**
     * 处理版本字符串，提取数字部分
     */
     fun processVersionString(versionString: String): String {
        // 匹配版本号中的数字部分 (例如从 "7.18（stable）" 提取 "7.18")
        val regex = "(\\d+\\.\\d+)".toRegex()
        val matchResult = regex.find(versionString)

        return matchResult?.value ?: versionString
    }
}