package com.example.trips.infrastructure.rest;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


class TokenAuthenticationFilter extends OncePerRequestFilter {

  private static final int TOKEN_VALUE_INDEX = 7;

  private final AuthenticationProperties authenticationProperties;

  public TokenAuthenticationFilter(AuthenticationProperties authenticationProperties) {
    this.authenticationProperties = authenticationProperties;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    Optional<String> bearer = extractBearer(request);
    if (bearer.isPresent()) {
      String token = bearer.get();
      if (!Objects.equals(authenticationProperties.getSecret(), token)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Bearer token value");
        return;
      }
      setUpSecurityContext(token);
    }
    filterChain.doFilter(request, response);
  }

  private Optional<String> extractBearer(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader("Authorization"))
        .filter(StringUtils::hasText)
        .filter(value -> value.startsWith("Bearer "))
        .map(value -> value.substring(TOKEN_VALUE_INDEX))
        .filter(StringUtils::hasText);
  }

  private void setUpSecurityContext(String token) {
    Authentication auth = new UsernamePasswordAuthenticationToken(null, token);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
