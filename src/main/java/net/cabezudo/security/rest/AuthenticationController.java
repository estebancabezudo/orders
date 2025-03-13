package net.cabezudo.security.rest;

import lombok.AllArgsConstructor;
import net.cabezudo.orders.exceptions.InvalidStatusException;
import net.cabezudo.orders.rest.APIResponse;
import net.cabezudo.orders.rest.OrderResponse;
import net.cabezudo.security.JwtManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {
  private final AuthenticationManager authenticationManager;
  private final JwtManager jwtManager;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequestData loginRequestData) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequestData.getUsername(),
            loginRequestData.getPassword()
        )
    );
    User user = (User) authentication.getPrincipal();
    String token = jwtManager.generateToken(user);
    RestToken restToken = new RestToken(token);
    return ResponseEntity.ok(new AuthenticationResponse(APIResponse.StatusCode.OK, "Login successfully", restToken));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<AuthenticationResponse> handleBadCredentials(BadCredentialsException e) {
    return ResponseEntity.badRequest().body(new AuthenticationResponse(APIResponse.StatusCode.NOT_AUTHORIZED, e.getMessage(), null));
  }

}