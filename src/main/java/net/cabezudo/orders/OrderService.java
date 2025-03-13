package net.cabezudo.orders;

import lombok.SneakyThrows;
import net.cabezudo.orders.exceptions.EntityNotFoundException;
import net.cabezudo.orders.exceptions.InvalidStatusException;
import net.cabezudo.orders.exceptions.OrderNotFoundException;
import net.cabezudo.orders.exceptions.ProductNotAvailableException;
import net.cabezudo.orders.persistence.OrderEntity;
import net.cabezudo.orders.persistence.OrderRepository;
import net.cabezudo.products.ProductService;
import net.cabezudo.products.exceptions.ProductWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
  private ProductService productService;
  private OrderRepository orderRepository;
  private OrderMapper orderMapper;

  @Autowired
  public void set(ProductService productService, OrderRepository orderRepository, OrderMapper orderMapper) {
    this.productService = productService;
    this.orderRepository = orderRepository;
    this.orderMapper = orderMapper;
  }

  public Order save(Order order) throws ProductNotAvailableException, ProductWebServiceException {
    OrderEntity orderEntity = orderMapper.toEntity(order);
    if (!productService.areAvailable(order.getProductId(), order.getQuantity())) {
      throw new ProductNotAvailableException(order.getProductId(), order.getQuantity());
    }
    OrderEntity newOrderEntity = orderRepository.save(orderEntity);
    return orderMapper.toBusiness(newOrderEntity);
  }

  @SneakyThrows
  @Cacheable(value = "ordersCache", key = "#id")
  public Order findById(Long id) {
    Optional<OrderEntity> orderOptional = orderRepository.findById(id);
    if (orderOptional.isEmpty()) {
      throw new EntityNotFoundException(id);
    }
    return orderMapper.toBusiness(orderOptional.get());
  }

  public OrderList findAll() throws ProductWebServiceException {
    List<OrderEntity> entityList = (List<OrderEntity>) orderRepository.findAll();
    return orderMapper.toBusiness(entityList);
  }

  @CacheEvict(value = "ordersCache", key = "#id")
  public Order updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException, InvalidStatusException {
    Order order;
    try {
      order = findById(id);
    } catch (EntityNotFoundException e) {
      throw new OrderNotFoundException(id, e);
    }
    order.changeStatus(newStatus);
    OrderEntity orderEntity = orderMapper.toEntity(order);
    orderRepository.save(orderEntity);

    return order;
  }

  public OrderList findByCustomer(Long customerId) throws ProductWebServiceException {
    List<OrderEntity> entityList = orderRepository.findByCustomerId(customerId);
    return orderMapper.toBusiness(entityList);
  }
}
