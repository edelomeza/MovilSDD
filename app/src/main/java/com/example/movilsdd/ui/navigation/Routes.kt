package com.example.movilsdd.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class LoginRoute(val error: String? = null)

@Serializable
data class HomeRoute(val username: String)
