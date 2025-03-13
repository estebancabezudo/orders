package net.cabezudo.orders.rest;

public class EntityNotFoundResponse extends APIResponse<Long>{
  public EntityNotFoundResponse(StatusCode code, String message, Long data) {
    super(code, message, data);
  }
}
