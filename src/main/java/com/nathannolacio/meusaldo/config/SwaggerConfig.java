package com.nathannolacio.meusaldo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Meu Saldo API",
                version = "1.0",
                description = "API REST para gerencimento financeiro pessoal. Endpoint para cadastro de usuários, controle de contas, transações e cálculo de saldo."
        )
)
public class SwaggerConfig {
}
