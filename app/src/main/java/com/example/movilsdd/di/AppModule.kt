package com.example.movilsdd.di

import org.koin.core.module.Module

val appModules: List<Module> = listOf(coreModule, dataModule, featureModule)
