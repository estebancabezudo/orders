package net.cabezudo.security.rest;

import lombok.Data;

@Data
public class LoginRequestData {
  private String username;
  private String password;
}

