package net.cabezudo.orders.rest;

public class OrderResponse extends APIResponse<RestOrder> {
  public OrderResponse(StatusCode code, String message, RestOrder data) {
    super(code, message, data);
  }

  public OrderResponse(StatusCode code, String orderUpdated) {
    super(code, orderUpdated, null);
  }
}
