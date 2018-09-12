package com.solstice.orderorderlines.service;

import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderOrderLineService {

  public List<Order> getOrders() {
    return Arrays.asList(new Order());
  }

  public Order createOrder(String data) {
    return new Order();
  }

  public Order updateOrder(long id, String body) {
    return new Order();
  }

  public Order deleteOrder(long anyLong) {
    return new Order();
  }

  public List<OrderLineItem> getOrderLineItems(long id) {
    return Arrays.asList(new OrderLineItem());
  }

  public OrderLineItem createOrderLineItem(long id, String body) {
    return new OrderLineItem();
  }

  public OrderLineItem updateOrderLineItem(long orderId, long orderLineId, String body) {
    return new OrderLineItem();
  }

  public OrderLineItem deleteOrderLineItem(long anyLong, long anyLong1) {
    return new OrderLineItem();
  }
}
