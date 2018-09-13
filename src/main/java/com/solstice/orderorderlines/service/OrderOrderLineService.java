package com.solstice.orderorderlines.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.dao.OrderLineItemRepository;
import com.solstice.orderorderlines.dao.OrderRepository;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderOrderLineService {

  private OrderLineItemRepository orderLineItemRepository;
  private OrderRepository orderRepository;
  private ObjectMapper objectMapper;

  public OrderOrderLineService(OrderRepository orderRepository,
      OrderLineItemRepository orderLineItemRepository) {
    this.orderRepository = orderRepository;
    this.orderLineItemRepository = orderLineItemRepository;
    objectMapper = new ObjectMapper();
  }

  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  public Order getOrderById(long id) {
    return orderRepository.findByOrderNumber(id);
  }

  public Order createOrder(String data) throws IOException {
    Order order = objectMapper.readValue(data, Order.class);
    orderRepository.save(order);
    return order;
  }

  public Order updateOrder(long id, String body) throws IOException {
    Order updatedOrder = objectMapper.readValue(body, Order.class);
    if(updatedOrder == null || orderRepository.findByOrderNumber(id) == null) {
      return null;
    }
    updatedOrder.setOrderNumber(id);
    orderRepository.save(updatedOrder);
    return updatedOrder;
  }

  public Order deleteOrder(long id) {
    Order deletedOrder = getOrderById(id);
    orderRepository.delete(deletedOrder);
    return deletedOrder;
  }

  public List<OrderLineItem> getOrderLineItems(long id) {
    return orderRepository.findOrderLineItemsByOrderNumber(id);
  }

  public OrderLineItem createOrderLineItem(long id, String body) throws IOException {
    OrderLineItem orderLineItem = null;
    Order order = orderRepository.findByOrderNumber(id);
    if(order != null) {
      orderLineItem = objectMapper.readValue(body, OrderLineItem.class);
      order.addOrderLineItem(orderLineItem);
      orderRepository.save(order);
    }
    return orderLineItem;
  }

  public OrderLineItem updateOrderLineItem(long orderId, long orderLineId, String body)
      throws IOException {
    OrderLineItem updateOrderLineItem = null;
    OrderLineItem dbOrderLineItem = orderLineItemRepository
        .findOrderLineItemByIdAndOrderId(orderLineId, orderId);
    if (dbOrderLineItem != null) {
      updateOrderLineItem = objectMapper.readValue(body, OrderLineItem.class);
      updateOrderLineItem.setId(orderId);
      orderLineItemRepository.save(updateOrderLineItem);
    }
    return updateOrderLineItem;
  }

  public OrderLineItem deleteOrderLineItem(long orderId, long orderLineId) {
    OrderLineItem deletedOrderLineItem = orderLineItemRepository
        .findOrderLineItemByIdAndOrderId(orderLineId, orderId);
    orderLineItemRepository.delete(deletedOrderLineItem);
    return deletedOrderLineItem;
  }

  public List<Order> getOrdersByAccountId(long accountId) {
    return orderRepository.findAllByAccountId(accountId);
  }
}
