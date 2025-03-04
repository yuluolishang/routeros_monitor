package com.yuanshan.routeros.data

data class InterfaceInfo(
    val name: String,
    val type: String,
    val status: String,
    val rxRate: String = "0", // 接收速率
    val txRate: String = "0"  // 发送速率
)