package net.cabezudo.orders.exceptions;

import lombok.Getter;
import net.cabezudo.orders.OrderStatus;

@Getter
public class InvalidStatusException extends Exception {
  private final OrderStatus oldStatus;
  private final OrderStatus newStatus;

  public InvalidStatusException(OrderStatus oldStatus, OrderStatus newStatus) {
    super("You cannot change from " + oldStatus + " to " + newStatus);
    this.oldStatus = oldStatus;
    this.newStatus = newStatus;
  }
}
