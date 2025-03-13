package net.cabezudo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private String secretKey = "kjahsdfñkasjhdfñkasdjñakjhbdfñby7qp938475npqc846np5q387n45pq783ynp5qx764yp58q736nxq876q84635p8q37";
  private AuthenticationWebService authenticationWebService;

  @Autowired
  private void set(AuthenticationWebService authenticationWebService) {
    this.authenticationWebService = authenticationWebService;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      UserDTO user = authenticationWebService.getByUsername(username);
      if (user == null) {
        throw new UsernameNotFoundException("User not found: " + username);
      }
      return User.withUsername(user.getUsername())
          .password(user.getPassword())
          .roles(user.getRole())
          .build();
    };
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return parameters -> {
      try {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");
        JwtClaimsSet claims = parameters.getClaims();
        ObjectMapper mapper = new ObjectMapper();
        String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(headers));
        String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(claims.getClaims()));
        String dataToSign = headerEncoded + "." + payloadEncoded;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] calculatedSignature = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(calculatedSignature);
        String tokenValue = headerEncoded + "." + payloadEncoded + "." + signature;

        return Jwt.withTokenValue(tokenValue)
            .headers(h -> h.putAll(headers))
            .claims(c -> c.putAll(claims.getClaims()))
            .build();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}