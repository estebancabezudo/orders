package net.cabezudo.orders.rest;

import java.util.List;

public class OrderListResponse extends APIResponse<List<RestOrder>> {
  public OrderListResponse(StatusCode status, String message, List<RestOrder> data) {
    super(status, message, data);
  }
}
