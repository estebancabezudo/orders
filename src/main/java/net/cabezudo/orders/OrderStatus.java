package net.cabezudo.orders;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum OrderStatus {
  PENDING, SHIPPED, DELIVERED;

  public static String toListString() {
      return Arrays.stream(OrderStatus.values())
          .map(Enum::name)
          .collect(Collectors.joining(", "));
    }
}
