package com.example.movilsdd.di

import com.example.movilsdd.data.local.PreferencesLocalDataSource
import com.example.movilsdd.data.local.PreferencesLocalDataSourceImpl
import com.example.movilsdd.data.local.SessionManagerImpl
import com.example.movilsdd.data.local.TokenLocalDataSource
import com.example.movilsdd.data.local.TokenLocalDataSourceImpl
import com.example.movilsdd.data.remote.AuthApiService
import com.example.movilsdd.data.remote.AuthRemoteDataSource
import com.example.movilsdd.data.remote.AuthRemoteDataSourceImpl
import com.example.movilsdd.data.repository.AuthRepositoryImpl
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.domain.usecase.LoginUseCase
import com.example.movilsdd.domain.usecase.LogoutUseCase
import com.example.movilsdd.ui.feature.login.LoginViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val loginModule = module {
    single<AuthApiService> {
        val config: AppConfig = get()
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .connectTimeout(config.networkTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.networkTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.networkTimeoutSeconds, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(config.authBaseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApiService::class.java)
    }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(api = get()) }
    single<TokenLocalDataSource> { TokenLocalDataSourceImpl(context = androidContext()) }
    single<PreferencesLocalDataSource> { PreferencesLocalDataSourceImpl(context = androidContext()) }
    single<SessionManager> { SessionManagerImpl(tokenStore = get()) }
    single<AuthRepository> { AuthRepositoryImpl(remote = get(), sessionManager = get()) }
    factory<LoginUseCase> { LoginUseCase(authRepository = get(), sessionManager = get()) }
    factory<LogoutUseCase> { LogoutUseCase(authRepository = get(), sessionManager = get()) }
    viewModel<LoginViewModel> { LoginViewModel(loginUseCase = get()) }
}
