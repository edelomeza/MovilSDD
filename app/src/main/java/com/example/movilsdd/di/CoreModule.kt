package com.example.movilsdd.di

import org.koin.dsl.module

val coreModule = module {
    single<LoggerHolder> { LoggerHolder() }
    single<AppConfig> { AppConfig() }
}

class LoggerHolder

data class AppConfig(val networkTimeoutSeconds: Long = 60L, val defaultPageSize: Int = 20)
