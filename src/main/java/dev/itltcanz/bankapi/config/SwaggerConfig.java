package dev.itltcanz.bankapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

  /**
   * Configures the OpenAPI specification for the Bank API.
   *
   * @return the configured OpenAPI instance
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("Bank API").version("1.0")
            .description("API documentation for the Bank API"))
        .addSecurityItem(new SecurityRequirement().addList("BearerAuth")).components(
            new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
                new SecurityScheme().name("BearerAuth").type(SecurityScheme.Type.HTTP)
                    .scheme("bearer").bearerFormat("JWT")));
  }
}