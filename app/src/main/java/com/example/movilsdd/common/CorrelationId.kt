package com.example.movilsdd.common

import java.util.UUID

@JvmInline
value class CorrelationId(val value: String) {
    companion object {
        fun new(): CorrelationId = CorrelationId(UUID.randomUUID().toString())
    }
}
