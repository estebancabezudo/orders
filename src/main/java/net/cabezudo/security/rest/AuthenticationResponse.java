package net.cabezudo.security.rest;

import net.cabezudo.orders.rest.APIResponse;

public class AuthenticationResponse extends APIResponse<RestToken> {
  public AuthenticationResponse(StatusCode status, String message, RestToken restToken) {
    super(status, message, restToken);
  }
}