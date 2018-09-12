package com.solstice.orderorderlines.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.dao.OrderLineItemRepository;
import com.solstice.orderorderlines.dao.OrderRepository;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderOrderLineService {

  private Logger logger = LoggerFactory.getLogger(OrderOrderLineService.class);

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

  public Order createOrder(String data) {
    Order order = getOrderFromJson(data);
    orderRepository.save(order);
    return order;
  }

  public Order updateOrder(long id, String body) {
    Order updatedOrder = getOrderFromJson(body);
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

  public OrderLineItem createOrderLineItem(long id, String body) {
    OrderLineItem orderLineItem = getOrderLineItemFromJson(body);
    Order order = orderRepository.findByOrderNumber(id);
    if(orderLineItem != null && order != null) {
      order.addOrderLineItem(orderLineItem);
      orderRepository.save(order);
    }
    return orderLineItem;
  }

  public OrderLineItem updateOrderLineItem(long orderId, long orderLineId, String body) {
    OrderLineItem updateOrderLineItem = null;
    OrderLineItem dbOrderLineItem = orderLineItemRepository
        .findOrderLineItemByIdAndOrderId(orderLineId, orderId);
    if (dbOrderLineItem != null) {
      updateOrderLineItem = getOrderLineItemFromJson(body);
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

  private Order getOrderFromJson(String json) {
    Order order = null;
    try {
      order = objectMapper.readValue(json, Order.class);
    } catch (IOException e) {
      logger.error("IOException thrown in getAccountFromJson: {}", e.toString());
    }
    return order;
  }

  private OrderLineItem getOrderLineItemFromJson(String json) {
    OrderLineItem orderLineItem = null;
    try {
      orderLineItem = objectMapper.readValue(json, OrderLineItem.class);
    } catch (IOException e) {
      logger.error("IOException thrown in getAccountFromJson: {}", e.toString());
    }
    return orderLineItem;
  }
}
