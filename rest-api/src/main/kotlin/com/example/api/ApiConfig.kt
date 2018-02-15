package com.example.api

import com.example.config.EnvironmentName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ApiConfig(
        @Value(value = "\${app.serviceName}") val serviceName: String,
        @Value(value = "\${app.environmentName}") val environmentName: EnvironmentName
) {
    val title: String
        get() = "API $serviceName ($environmentName)"
}