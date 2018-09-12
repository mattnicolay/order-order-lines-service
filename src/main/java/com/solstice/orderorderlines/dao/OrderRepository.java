package com.solstice.orderorderlines.dao;

import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<Order, Long> {

  List<Order> findAll();

  Order findByOrderNumber(long orderNumber);

  @Query("select o.orderLineItems from Order o where o.id = :id")
  List<OrderLineItem> findOrderLineItemsByOrderNumber(@Param("id") long id);
}
