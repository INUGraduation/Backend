package com.example.inu.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean // OpenAPI는 Swagger 3.0 사양을 구현하는 객체
    public OpenAPI openAPI() {
        // Info 객체는 api의 기본 정보를 설정
        Info info = new Info()
                .version("v1.0.0")
                .title("APIs of INU")
                .description("sprigndoc을 이용한 스웨거 api 문서화입니다.");

        String jwtSchemaName = "jwtAuth";
        // Header에 인증정보 포함 (jwt 인증이 필요함을 명시)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemaName);

        // Components 객체를 사용하여 JWT 인증 스키마를 정의하고 모든 API 엔드포인트에서 참조할 수 있도록 함
        Components components = new Components()
                // SecurityShceme를 통해 인증 방식을 설정함
                .addSecuritySchemes(jwtSchemaName, new SecurityScheme()
                        .name(jwtSchemaName) // 스키마의 이름 설정
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer") // 사용하는 스키마는 bearer
                        .bearerFormat("JWT")); // bearer 토큰 형식은 JWT

        // 위의 정보가 포함된 OpenAPI 객체 반환
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}

