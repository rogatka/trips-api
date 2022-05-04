package com.example.trips.infrastructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${web-security.debug-enabled}")
  private boolean isDebugEnabled;

  private final AuthenticationProperties authenticationProperties;

  SecurityConfiguration(AuthenticationProperties authenticationProperties) {
    this.authenticationProperties = authenticationProperties;
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() {
    return authentication -> authentication;
  }

  @Override
  public void configure(WebSecurity web) {
    web.debug(isDebugEnabled);
    web.ignoring()
      .antMatchers(HttpMethod.GET, "/error");
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

    httpSecurity.addFilterBefore(new TokenAuthenticationFilter(authenticationProperties), LogoutFilter.class);
  }
}
