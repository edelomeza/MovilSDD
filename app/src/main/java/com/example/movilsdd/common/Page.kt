package com.example.movilsdd.common

data class Page<T>(val items: List<T> = emptyList(), val page: Int = 0, val pageSize: Int = 20, val total: Long = 0L)
