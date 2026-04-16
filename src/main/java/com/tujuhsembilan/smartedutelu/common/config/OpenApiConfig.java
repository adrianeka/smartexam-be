package com.tujuhsembilan.smartedutelu.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SmartEdu API")
                        .version("1.0")
                        .description("Dokumentasi API untuk sistem backend SmartEdu"))
                // Mewajibkan setiap endpoint API untuk menyertakan skema keamanan ini secara default
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Mendefinisikan format skema keamanan JWT Bearer
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    /**
     * J11: Tambah response code standar ke semua operasi secara global.
     * Individual endpoint dapat override dengan {@code @ApiResponse} sendiri.
     */
    @Bean
    public OperationCustomizer globalApiResponses() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }
            responses.putIfAbsent("400", new ApiResponse().description("Request tidak valid — validasi gagal atau parameter salah"));
            responses.putIfAbsent("401", new ApiResponse().description("Tidak terautentikasi — token JWT tidak ada atau expired"));
            responses.putIfAbsent("403", new ApiResponse().description("Tidak punya akses — role tidak mencukupi"));
            responses.putIfAbsent("404", new ApiResponse().description("Data tidak ditemukan"));
            responses.putIfAbsent("429", new ApiResponse().description("Terlalu banyak request — rate limit terlampaui"));
            responses.putIfAbsent("500", new ApiResponse().description("Internal server error"));
            return operation;
        };
    }
}
