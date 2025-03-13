package net.cabezudo.orders.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
  @Override
  OrderEntity save(OrderEntity entity);

  Optional<OrderEntity> findById(Long id);

  List<OrderEntity> findByCustomerId(Long customerId);
}
