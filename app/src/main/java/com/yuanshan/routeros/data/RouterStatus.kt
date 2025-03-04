package com.yuanshan.routeros.data

data class RouterStatus(
    val cpuLoad: String = "",
    val memoryTotal: String = "",
    val memoryFree: String = "",
    val uptime: String = "",
    val version: String = ""
)