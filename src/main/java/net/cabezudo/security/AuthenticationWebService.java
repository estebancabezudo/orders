package net.cabezudo.security;

import org.springframework.stereotype.Service;

/**
 * This service connect to a remote web service for authentication. Now just hardcoded username/password
 */
@Service
public class AuthenticationWebService {
  public UserDTO getByUsername(String username) {
    return new UserDTO("username", "$2a$10$FxzddC8i3sCTsYbKS8O7m.MNm930SCp//SjD53fnKGyTOu.u15/2O", "USER");
  }
}
