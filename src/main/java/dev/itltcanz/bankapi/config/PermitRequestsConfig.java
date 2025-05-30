package dev.itltcanz.bankapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
@Configuration
public class PermitRequestsConfig {

    @Bean
    public RequestMatcher permitRequests() {
        return new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher("/v1/auth/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/api-docs/**")
        );
    }
}
