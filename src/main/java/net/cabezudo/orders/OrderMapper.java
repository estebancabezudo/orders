package net.cabezudo.orders;

import lombok.SneakyThrows;
import net.cabezudo.orders.exceptions.InvalidOrderParameterException;
import net.cabezudo.orders.persistence.OrderEntity;
import net.cabezudo.orders.rest.RestOrder;
import net.cabezudo.products.ProductService;
import net.cabezudo.products.exceptions.ProductWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
// I like to be explicit in the mappers because I believe it reduces errors and makes maintenance easier.
// It's easier to read, and if something like cacheManager needs to be added for conversion at any point,
// it greatly reduces the cost of the change.
public class OrderMapper {

  private ProductService productService;

  @Autowired
  public void set(ProductService productService) {
    this.productService = productService;
  }

  public Order toBusiness(RestOrder restOrder) throws InvalidOrderParameterException, ProductWebServiceException {
    Long id = restOrder.getId();
    Long customerId = restOrder.getCustomerId();
    Long productId = restOrder.getProductId();
    String productDetails = productService.getDescription(productId);
    Integer quantity = restOrder.getQuantity();
    BigDecimal price = restOrder.getPrice();
    OrderStatus status;
    if (restOrder.getStatus() == null) {
      status = OrderStatus.PENDING;
    } else {
      try {
        status = OrderStatus.valueOf(restOrder.getStatus());
      } catch (IllegalArgumentException e) {
        throw new InvalidOrderParameterException("status", restOrder.getStatus());
      }
    }
    LocalDateTime createdAt = restOrder.getCreatedAt();
    LocalDateTime updatedAt = restOrder.getUpdatedAt();

    return new Order(id, customerId, productId, productDetails, quantity, price, status, createdAt, updatedAt);
  }

  public RestOrder toRest(Order order) throws ProductWebServiceException {
    Long id = order.getId();
    Long customerId = order.getCustomerId();
    Long productId = order.getProductId();
    String productDetails = productService.getDescription(productId);
    Integer quantity = order.getQuantity();
    BigDecimal price = order.getPrice();
    String status = order.getStatus().name();
    LocalDateTime createdAt = order.getCreatedAt();
    LocalDateTime updatedAt = order.getUpdatedAt();

    return new RestOrder(id, customerId, productId, productDetails, quantity, price, status, createdAt, updatedAt);
  }

  public Order toBusiness(OrderEntity orderEntity) throws ProductWebServiceException {
    Long id = orderEntity.getId();
    Long customerId = orderEntity.getCustomerId();
    Long productId = orderEntity.getProductId();
    String productDetails = productService.getDescription(productId);
    Integer quantity = orderEntity.getQuantity();
    BigDecimal price = orderEntity.getPrice();
    OrderStatus status;
    status = orderEntity.getStatus();
    LocalDateTime createdAt = orderEntity.getCreatedAt();
    LocalDateTime updatedAt = orderEntity.getUpdatedAt();

    return new Order(id, customerId, productId, productDetails, quantity, price, status, createdAt, updatedAt);
  }

  public OrderEntity toEntity(Order order) {
    Long id = order.getId();
    Long customerId = order.getCustomerId();
    Long productId = order.getProductId();
    Integer quantity = order.getQuantity();
    BigDecimal price = order.getPrice();
    OrderStatus status = order.getStatus();
    LocalDateTime createdAt = order.getCreatedAt();
    LocalDateTime updatedAt = order.getUpdatedAt();

    return new OrderEntity(id, customerId, productId, quantity, price, status, createdAt, updatedAt);
  }

  public OrderList toBusiness(List<OrderEntity> entityList) throws ProductWebServiceException {
    OrderList orderList = new OrderList();
    for (OrderEntity entity : entityList) {
      Order order = toBusiness(entity);
      orderList.add(order);
    }
    return orderList;
  }

  @SneakyThrows
  public List<RestOrder> toRest(OrderList orderList) {
    List<RestOrder> list = new ArrayList<>();
    for (Order order : orderList) {
      list.add(toRest(order));
    }
    return list;
  }
}

