package net.cabezudo.orders.exceptions;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
  @Getter
  private final Long entityId;

  public EntityNotFoundException(Long entityId) {
    this.entityId = entityId;
  }
}
