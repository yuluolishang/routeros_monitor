package com.yuanshan.routeros.utils

object ByteFormatterUtil {

    fun formatBytes(bytes: String): String {
        val bytesLong = bytes.toLongOrNull() ?: 0
        return formatBytes(bytesLong)
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format("%.1f %sB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
    }
}