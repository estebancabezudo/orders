package net.cabezudo.orders;

import net.cabezudo.orders.rest.APIResponse;
import net.cabezudo.orders.rest.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<OrderResponse> handleNoResourceFoundException(Exception e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new OrderResponse(APIResponse.StatusCode.RESOURCE_NOT_FOUND, e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<OrderResponse> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity.internalServerError().body(new OrderResponse(APIResponse.StatusCode.OK, e.getMessage()));
  }
}
