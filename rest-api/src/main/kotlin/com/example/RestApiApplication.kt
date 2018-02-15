package com.example

import com.example.api.ApiConfig
import mu.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener


@SpringBootApplication
class RestApiApplication(private val apiConfig: ApiConfig) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(contextRefreshedEvent: ApplicationReadyEvent) {
        logger.info("=== STARTED SPRING BOOT APP: service=${apiConfig.serviceName} env=${apiConfig.environmentName} ===")
        System.gc()
    }

    companion object : KLogging()
}