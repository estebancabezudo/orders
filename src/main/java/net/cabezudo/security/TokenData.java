package net.cabezudo.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
public class TokenData implements Authentication {
  private final String username;
  private final List<String> groups;
  private boolean authenticated;

  public TokenData(String username, List<String> groups) {
    this.username = username;
    this.groups = groups;
    this.authenticated = true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return groups.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public Object getCredentials() {
    // No hay credenciales porque usamos JWT
    return null;
  }

  @Override
  public Object getDetails() {
    // Puedes devolver información adicional si lo necesitas
    return null;
  }

  @Override
  public Object getPrincipal() {
    return username; // El principal es el nombre de usuario
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
    this.authenticated = authenticated;
  }

  @Override
  public String getName() {
    return username; // El nombre del usuario
  }
}
