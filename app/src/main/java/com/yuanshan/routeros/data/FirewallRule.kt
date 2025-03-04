package com.yuanshan.routeros.data

data class FirewallRule(
    val id: String,
    val chain: String,
    val action: String,
    val bytes: String,
    val packets: String,
    val comment: String? = null,
    val disabled: Boolean = false
)