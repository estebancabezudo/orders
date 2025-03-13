package net.cabezudo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtManager {
  private JwtEncoder jwtEncoder;
  private JwtDecoder jwtDecoder;

  @Autowired
  public void set(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
    this.jwtEncoder = jwtEncoder;
    this.jwtDecoder = jwtDecoder;
  }

  public TokenData validateTokenAndGetUsername(String token) {
    Jwt jwt = this.jwtDecoder.decode(token);
    String username = jwt.getSubject();
    List<String> authorities = jwt.getClaimAsStringList("authorities");
    return new TokenData(username, authorities);
  }

  public String generateToken(UserDetails user) {
    String username = user.getUsername();
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .subject(username)
        .build();
    return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }
}
