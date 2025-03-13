package net.cabezudo.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.cabezudo.orders.exceptions.InvalidStatusException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Order implements Serializable {
  private Long id;
  private Long customerId;
  private Long productId;
  private String productDetails;
  private Integer quantity;
  private BigDecimal price;
  private OrderStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public void changeStatus(OrderStatus newStatus) throws InvalidStatusException {

    boolean firstOrderValidationRule = status.equals(OrderStatus.PENDING) && newStatus.equals(OrderStatus.SHIPPED);
    boolean secondOrderValidationRule = status.equals(OrderStatus.SHIPPED) && newStatus.equals(OrderStatus.DELIVERED);
    if (!newStatus.equals(status)) {
      if (firstOrderValidationRule || secondOrderValidationRule) {
        status = newStatus;
      } else {
        throw new InvalidStatusException(status, newStatus);
      }
    }

  }
}
