package com.example.config

import com.example.api.ApiConfig
import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class Swagger(private val apiConfig: ApiConfig) {

    @Bean
    fun mainApi(): Docket = apiConfig.toDocket()
            .groupName("${apiConfig.serviceName} (Main)")
            .select()
            .apis(RequestHandlerSelectors.basePackage(apiConfig.getBasePackageName()))
            .build()

    @Bean
    fun monitoringApi(): Docket = apiConfig.toDocket()
            .groupName("${apiConfig.serviceName} (Monitoring)")
            .useDefaultResponseMessages(true)
            .select()
            .apis(Predicates.not(RequestHandlerSelectors.basePackage(apiConfig.getBasePackageName())))
            .build()
}

private fun ApiConfig.getBasePackageName() = this::class.java.`package`.name
private fun ApiConfig.toApiInfo() = ApiInfoBuilder().title(this.title).build()
private fun ApiConfig.toDocket() = Docket(DocumentationType.SWAGGER_2).apiInfo(this.toApiInfo())