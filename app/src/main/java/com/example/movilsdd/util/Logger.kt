package com.example.movilsdd.util

import android.util.Log
import com.example.movilsdd.BuildConfig
import com.example.movilsdd.common.CorrelationId

object Logger {
    private val secretPattern =
        Regex("(?i)(token|secret|password|passwd|api[_-]?key|authorization)[\"']?\\s*[:=]\\s*[\"']?[^\\s\"',}]+")

    fun sanitize(message: String): String = secretPattern.replace(message, "$1=***")

    fun d(tag: String, message: String, correlationId: CorrelationId? = null) {
        if (BuildConfig.DEBUG) Log.d(tag, withId(message, correlationId))
    }

    fun i(tag: String, message: String, correlationId: CorrelationId? = null) {
        Log.i(tag, withId(sanitize(message), correlationId))
    }

    fun w(tag: String, message: String, correlationId: CorrelationId? = null) {
        Log.w(tag, withId(sanitize(message), correlationId))
    }

    fun e(tag: String, message: String, throwable: Throwable? = null, correlationId: CorrelationId? = null) {
        val safe = withId(sanitize(message), correlationId)
        if (BuildConfig.DEBUG && throwable != null) Log.e(tag, safe, throwable) else Log.e(tag, safe)
    }

    private fun withId(message: String, correlationId: CorrelationId?): String =
        if (correlationId == null) sanitize(message) else "[${correlationId.value}] ${sanitize(message)}"

    @Synchronized
    fun reset() = Unit
}
