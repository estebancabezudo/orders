package net.cabezudo.orders.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIResponse<T> {
  private final StatusCode code;
  private final String message;
  private final T data;
  public enum StatusCode {
    OK, ORDER_NOT_FOUND, RESOURCE_NOT_FOUND, INVALID_STATUS, NOT_ENOUGHT_PRODUCT, INVALID_PARAMETER, NOT_AUTHORIZED, BAD_GATEWAY;
  }
}
