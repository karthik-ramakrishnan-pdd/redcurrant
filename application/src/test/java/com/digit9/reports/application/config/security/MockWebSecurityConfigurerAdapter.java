package com.digit9.reports.application.config.security;

import com.digit9.commons.core.data.enums.UserRole;
import com.digit9.commons.web.security.handler.AccessDeniedExceptionHandler;
import com.digit9.commons.web.security.handler.AuthenticationExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Profile("test")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableResourceServer
public class MockWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    public static final String TSP_USER = "sp1";

    public static final String TSP_USER_PASS = "password";

    public static final String MERCHANT_USER = "ms1";

    public static final String MERCHANT_USER_PASS = "password";

    public static final String BASIC_USER = "basic";

    public static final String BASIC_USER_PASS = "password";

    public static final String USERNAME_KEY = "username";

    public static final String PASSWORD_KEY = "password";

    @Autowired
    private AuthenticationExceptionHandler authenticationExceptionHandler;

    @Autowired
    private AccessDeniedExceptionHandler accessDeniedExceptionHandler;

    @Override
    public void configure(final WebSecurity webSecurity) {
        webSecurity.ignoring()
            .antMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**",
                    "/digit9-merchant-transactions-service.html/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.authorizeRequests()
            .antMatchers("/oauth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**",
                    "/digit9-merchant-transactions-service.html/**")
            .permitAll()
            .antMatchers("/**")
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationExceptionHandler)
            .accessDeniedHandler(accessDeniedExceptionHandler)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .httpBasic()
            .disable();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser(TSP_USER)
            .password(passwordEncoder().encode(TSP_USER_PASS))
            .authorities(new SimpleGrantedAuthority(UserRole.SERVICE_PROVIDER.name()))
            .and()
            .withUser(MERCHANT_USER)
            .password(passwordEncoder().encode(MERCHANT_USER_PASS))
            .authorities(new SimpleGrantedAuthority(UserRole.MERCHANT_SERVICE.name()))
            .and()
            .withUser(BASIC_USER)
            .password(passwordEncoder().encode(BASIC_USER_PASS))
            .authorities(new SimpleGrantedAuthority("BASIC"));
    }

}
