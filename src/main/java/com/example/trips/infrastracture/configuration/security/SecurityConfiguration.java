package com.example.trips.infrastracture.configuration.security;

import com.example.trips.infrastracture.configuration.properties.AuthenticationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationProperties authenticationProperties;

    public SecurityConfiguration(AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Value("${web-security.debug-enabled}")
    private boolean isDebugEnabled;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() {
        return authentication -> authentication;
    }

    @Override
    public void configure(WebSecurity web) {
        web.debug(isDebugEnabled);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        super.configure(httpSecurity);
        httpSecurity
                .csrf().disable()
                .cors()
                .and()
                .headers()
                .xssProtection().xssProtectionEnabled(true)
                .and()
                .httpStrictTransportSecurity()
                .and()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());

        httpSecurity.addFilterBefore(new TokenAuthenticationFilter(authenticationProperties),
                        LogoutFilter.class);
    }
}
