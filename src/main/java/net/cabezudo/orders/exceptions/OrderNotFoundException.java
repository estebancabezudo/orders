package net.cabezudo.orders.exceptions;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends Exception {
  private final Long orderId;

  public OrderNotFoundException(Long orderId, Throwable cause) {
    super(cause);
    this.orderId = orderId;
  }
}
