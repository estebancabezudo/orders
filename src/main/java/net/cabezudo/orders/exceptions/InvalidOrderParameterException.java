package net.cabezudo.orders.exceptions;

import lombok.Getter;

@Getter
public class InvalidOrderParameterException extends Exception {
  private final String name;
  private final String value;

  public InvalidOrderParameterException(String message, String name, String value) {
    super(message);
    this.name = name;
    this.value = value;
  }

  public InvalidOrderParameterException(String name, String value) {
    super("Invalid value (" + value + ") for parameter '" + name + "'");
    this.name = name;
    this.value = value;
  }
}