package com.example.movilsdd.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FLAGS_STORE_NAME = "movilsdd_flags"
private const val FLAG_ONBOARDING_SEEN = "onboarding_seen"
private val Context.flagsStore by preferencesDataStore(name = FLAGS_STORE_NAME)

interface PreferencesLocalDataSource {
    val onboardingSeen: Flow<Boolean>

    suspend fun setOnboardingSeen(seen: Boolean)
}

class PreferencesLocalDataSourceImpl(context: Context) : PreferencesLocalDataSource {
    private val appContext: Context = context.applicationContext
    private val onboardingKey = booleanPreferencesKey(FLAG_ONBOARDING_SEEN)

    override val onboardingSeen: Flow<Boolean> = appContext.flagsStore.data.map { prefs -> prefs[onboardingKey] ?: false }

    override suspend fun setOnboardingSeen(seen: Boolean) {
        appContext.flagsStore.edit { prefs -> prefs[onboardingKey] = seen }
    }
}
