package com.example.movilsdd.shared

import java.util.UUID

object TestDataFactory {
    fun id(): String = UUID.randomUUID().toString()
    fun name(prefix: String = "item"): String = "$prefix-${UUID.randomUUID().toString().take(8)}"
    val words: String = "lorem ipsum dolor sit amet"
}
