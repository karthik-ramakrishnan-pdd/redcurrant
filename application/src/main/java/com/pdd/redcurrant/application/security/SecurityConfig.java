package com.pdd.redcurrant.application.security;

import com.pdd.redcurrant.application.security.handler.AccessDeniedExceptionHandler;
import com.pdd.redcurrant.application.security.handler.AuthenticationExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Configures Spring security.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@Profile("!test")
@ConditionalOnClass(HttpSecurity.class)
@AutoConfigureAfter(AuthenticationExceptionHandler.class)
public class SecurityConfig {

    protected static final String[] IGNORED_PATH = { "/v1/internal/**", "/v3/api-docs/**", "/swagger-resources/**",
            "/swagger-ui/**", "/error", "/actuator/**" };

    private final AuthenticationExceptionHandler authenticationExceptionHandler;

    private final AccessDeniedExceptionHandler accessDeniedExceptionHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authorize) -> authorize.requestMatchers(IGNORED_PATH).permitAll().anyRequest().authenticated());

        http.exceptionHandling(configurer -> {
            configurer.authenticationEntryPoint(authenticationExceptionHandler);
            configurer.accessDeniedHandler(accessDeniedExceptionHandler);
        });

        http.securityContext(securityContext -> securityContext.requireExplicitSave(true));

        http.sessionManagement(sessionMgmt -> sessionMgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(AbstractHttpConfigurer::disable);

        // CSRF configuration
        http.csrf(csrf -> csrf.ignoringRequestMatchers(IGNORED_PATH)
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }

}