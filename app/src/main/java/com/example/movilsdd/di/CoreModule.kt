package com.example.movilsdd.di

import com.example.movilsdd.BuildConfig
import org.koin.dsl.module

val coreModule = module {
    single<LoggerHolder> { LoggerHolder() }
    single<AppConfig> { AppConfig() }
}

class LoggerHolder

data class AppConfig(val networkTimeoutSeconds: Long = 60L, val defaultPageSize: Int = 20, val authBaseUrl: String = BuildConfig.AUTH_BASE_URL)
