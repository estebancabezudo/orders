package net.cabezudo.orders.rest;

import net.cabezudo.orders.Order;
import net.cabezudo.orders.OrderList;
import net.cabezudo.orders.OrderMapper;
import net.cabezudo.orders.OrderService;
import net.cabezudo.orders.OrderStatus;
import net.cabezudo.orders.exceptions.EntityNotFoundException;
import net.cabezudo.orders.exceptions.InvalidOrderParameterException;
import net.cabezudo.orders.exceptions.InvalidStatusException;
import net.cabezudo.orders.exceptions.OrderNotFoundException;
import net.cabezudo.orders.exceptions.ProductNotAvailableException;
import net.cabezudo.products.exceptions.ProductWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {
  private OrderMapper orderMapper;
  private OrderService orderService;

  @Autowired
  private void set(OrderMapper orderMapper, OrderService orderService) {
    this.orderMapper = orderMapper;
    this.orderService = orderService;
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderResponse> post(@RequestBody RestOrder restOrder) throws InvalidOrderParameterException, ProductNotAvailableException, ProductWebServiceException {
    validate(restOrder);
    Order order = orderMapper.toBusiness(restOrder);
    Order newOrder = orderService.save(order);
    RestOrder newRestOrder = orderMapper.toRest(newOrder);
    return ResponseEntity.ok(new OrderResponse(APIResponse.StatusCode.OK, "Order added successfully.", newRestOrder));
  }

  @GetMapping("/orders/{id}")
  public ResponseEntity<OrderResponse> get(@PathVariable Long id) throws ProductWebServiceException {
    Order order = orderService.findById(id);
    return ResponseEntity.ok(new OrderResponse(APIResponse.StatusCode.OK, "Order retrieved successfully.", orderMapper.toRest(order)));
  }

  @PatchMapping("/orders/{orderId}/status")
  public ResponseEntity<OrderResponse> patch(@RequestBody Map<String, String> body, @PathVariable Long orderId) throws OrderNotFoundException, InvalidStatusException, ProductWebServiceException {
    String newStatusName = body.get("status");
    if (newStatusName == null) {
      return ResponseEntity.badRequest()
          .body(new OrderResponse(APIResponse.StatusCode.INVALID_STATUS, "Status is required, pick one of [ " + OrderStatus.toListString() + " ]", null));
    }
    OrderStatus newStatus;
    try {
      newStatus = OrderStatus.valueOf(newStatusName);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(new OrderResponse(APIResponse.StatusCode.INVALID_STATUS, "Invalid status '" + newStatusName + "', must be one of [ " + OrderStatus.toListString() + " ]", null));
    }
    Order updatedOrder = orderService.updateStatus(orderId, newStatus);
    RestOrder updatedRestOrder = orderMapper.toRest(updatedOrder);
    return ResponseEntity.ok(new OrderResponse(APIResponse.StatusCode.OK, "Order status updated", updatedRestOrder));
  }

  private void validate(RestOrder restOrder) throws InvalidOrderParameterException {
    if (restOrder.getQuantity() == 0) {
      throw new InvalidOrderParameterException("The quantity property MUST NOT be 0", "quantity", "0");
    }
    // TODO ask for other validations for precondition values
  }

  // TODO Validate with the team that the resource name is correct. A customer has orders, so I believe it should be /customers/{customerId}/orders.
  @GetMapping("/orders/customer/{customerId}")
  public ResponseEntity<OrderListResponse> ordersByCustomer(@PathVariable Long customerId) throws ProductWebServiceException {
    OrderList orderList = orderService.findByCustomer(customerId);
    List<RestOrder> restOrderList = orderMapper.toRest(orderList);
    return ResponseEntity.ok(new OrderListResponse(APIResponse.StatusCode.OK, "The list of orders was successfully retrieved for customer: " + customerId, restOrderList));
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<OrderResponse> handleEmptyResultDataAccess() {
    return ResponseEntity.badRequest().body(new OrderResponse(APIResponse.StatusCode.RESOURCE_NOT_FOUND, "No se encontró el recurso"));
  }

  @ExceptionHandler(InvalidStatusException.class)
  public ResponseEntity<OrderResponse> handleInvalidStatus(InvalidStatusException e) {
    return ResponseEntity.badRequest().body(new OrderResponse(APIResponse.StatusCode.INVALID_STATUS, e.getMessage()));
  }

  @ExceptionHandler(InvalidOrderParameterException.class)
  public ResponseEntity<OrderResponse> handleInvalidOrderParameter(InvalidOrderParameterException e) {
    return ResponseEntity.badRequest().body(new OrderResponse(APIResponse.StatusCode.INVALID_PARAMETER, e.getMessage()));
  }

  @ExceptionHandler(ProductNotAvailableException.class)
  public ResponseEntity<OrderResponse> handleProductNotAvailable(ProductNotAvailableException e) {
    return ResponseEntity.badRequest().body(new OrderResponse(APIResponse.StatusCode.NOT_ENOUGHT_PRODUCT, e.getMessage()));
  }

  @ExceptionHandler(ProductWebServiceException.class)
  public ResponseEntity<OrderResponse> handleProductWebService(ProductWebServiceException e) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new OrderResponse(APIResponse.StatusCode.BAD_GATEWAY, e.getMessage()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<EntityNotFoundResponse> handleEntityNotFound(EntityNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        new EntityNotFoundResponse(APIResponse.StatusCode.ORDER_NOT_FOUND, "Entity with id " + e.getEntityId() + " NOT FOUND", e.getEntityId())
    );
  }
}
