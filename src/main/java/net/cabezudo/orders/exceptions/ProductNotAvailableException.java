package net.cabezudo.orders.exceptions;

import lombok.Getter;

@Getter
public class ProductNotAvailableException extends Throwable {
  private final Long productId;
  private final Integer quantity;

  public ProductNotAvailableException(Long productId, Integer quantity) {
    super("There are not enough products (" + productId + ") available (" + quantity + ")");
    this.productId = productId;
    this.quantity = quantity;
  }
}
