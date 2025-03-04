package com.yuanshan.routeros.config

import android.content.Context

object RouterConfig {
    private const val PREFS_NAME = "router_config"
    private const val DEFAULT_HOST = "192.168.8.1"
    private const val DEFAULT_PORT = "8728"
    private const val DEFAULT_USERNAME = "admin"

    fun getConfig(context: Context): RouterSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return RouterSettings(
            host = prefs.getString("host", DEFAULT_HOST) ?: DEFAULT_HOST,
            port = prefs.getString("port", DEFAULT_PORT) ?: DEFAULT_PORT,
            username = prefs.getString("username", DEFAULT_USERNAME) ?: DEFAULT_USERNAME,
            password = prefs.getString("password", "") ?: ""
        )
    }

    fun saveConfig(context: Context, settings: RouterSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("host", settings.host)
            putString("port", settings.port)
            putString("username", settings.username)
            putString("password", settings.password)
            apply()
        }
    }
}

data class RouterSettings(
    val host: String,
    val port: String,
    val username: String,
    val password: String
)
