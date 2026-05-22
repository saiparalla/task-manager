package com.project.taskmanager.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth")
                )

                .components(
                        new Components()

                                .addSecuritySchemes(
                                        "bearerAuth",

                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )

                .info(
                        new Info()
                                .title("Task Manager API")
                                .version("1.0")
                                .description("Task Manager Backend APIs")
                );
    }
}