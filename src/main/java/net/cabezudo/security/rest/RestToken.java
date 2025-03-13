package net.cabezudo.security.rest;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

@ToString
public class RestToken {
  private final TokenType type = TokenType.BEARER;
  @Getter
  private final String token;

  public RestToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type.getValue();
  }
}
