package net.cabezudo.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserDTO {
  private String username;
  private String password;
  private String role;
}
