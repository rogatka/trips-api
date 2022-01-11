package com.example.trips.infrastructure.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "auth")
@ConstructorBinding
class AuthenticationProperties {

  private final String secret;

  public AuthenticationProperties(String secret) {
    this.secret = secret;
  }

  public String getSecret() {
    return secret;
  }
}
