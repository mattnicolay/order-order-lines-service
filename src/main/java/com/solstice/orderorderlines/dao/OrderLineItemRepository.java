package com.solstice.orderorderlines.dao;

import com.solstice.orderorderlines.model.OrderLineItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderLineItemRepository extends CrudRepository<OrderLineItem, Long> {
  @Query(nativeQuery = true)
  OrderLineItem findOrderLineItemByIdAndOrderId(@Param("id") long id, @Param("orderId") long orderId);
}
