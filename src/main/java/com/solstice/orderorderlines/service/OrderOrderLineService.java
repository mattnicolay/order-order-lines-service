package com.solstice.orderorderlines.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.solstice.orderorderlines.dao.OrderLineItemRepository;
import com.solstice.orderorderlines.dao.OrderRepository;
import com.solstice.orderorderlines.external.AccountAddressClient;
import com.solstice.orderorderlines.external.ProductClient;
import com.solstice.orderorderlines.external.ShipmentClient;
import com.solstice.orderorderlines.model.Address;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderDetail;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.model.OrderLineSummary;
import com.solstice.orderorderlines.model.Product;
import com.solstice.orderorderlines.model.Shipment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderOrderLineService {

  private Logger logger = LoggerFactory.getLogger(OrderOrderLineService.class);

  private OrderLineItemRepository orderLineItemRepository;
  private OrderRepository orderRepository;
  private AccountAddressClient accountAddressClient;
  private ProductClient productClient;
  private ShipmentClient shipmentClient;
  private ObjectMapper objectMapper;

  public OrderOrderLineService(
      OrderLineItemRepository orderLineItemRepository,
      OrderRepository orderRepository,
      AccountAddressClient accountAddressClient,
      ProductClient productClient,
      ShipmentClient shipmentClient) {
    this.orderLineItemRepository = orderLineItemRepository;
    this.orderRepository = orderRepository;
    this.accountAddressClient = accountAddressClient;
    this.productClient = productClient;
    this.shipmentClient = shipmentClient;
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
    setPrices(order);
    orderRepository.save(order);
    return order;
  }

  public Order updateOrder(long id, String body) throws IOException {
    Order updatedOrder = objectMapper.readValue(body, Order.class);
    if(updatedOrder == null || orderRepository.findByOrderNumber(id) == null) {
      return null;
    }
    setPrices(updatedOrder);
    updatedOrder.setOrderNumber(id);
    orderRepository.save(updatedOrder);
    return updatedOrder;
  }

  public Order deleteOrder(long id) {
    Order deletedOrder = getOrderById(id);
    if (deletedOrder != null) {
      orderRepository.delete(deletedOrder);
    }
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
      setPrice(orderLineItem);
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
      setPrice(updateOrderLineItem);
      updateOrderLineItem.setId(orderId);
      orderLineItemRepository.save(updateOrderLineItem);
    }
    return updateOrderLineItem;
  }

  public OrderLineItem deleteOrderLineItem(long orderId, long orderLineId) {
    OrderLineItem deletedOrderLineItem = orderLineItemRepository
        .findOrderLineItemByIdAndOrderId(orderLineId, orderId);
    if (deletedOrderLineItem != null) {
      orderLineItemRepository.delete(deletedOrderLineItem);
    }
    return deletedOrderLineItem;
  }

  public List<Order> getOrdersByAccountId(long accountId) {
    return orderRepository.findAllByAccountIdOrderByOrderDate(accountId);
  }

  public List<OrderDetail> getOrderDetails(long accountId) {
    List<OrderDetail> orderDetails = new ArrayList<>();
    List<Order> orders = orderRepository.findAllByAccountIdOrderByOrderDate(accountId);

    orders.forEach(order -> {

      Address address = accountAddressClient.getAddressByAccountIdAndAddressId(
          order.getAccountId(), order.getShippingAddressId());
      logger.info("Address from feign client: {}", address);
      List<OrderLineItem> orderLineItems = orderRepository
          .findOrderLineItemsByOrderNumber(order.getOrderNumber());

      List<Shipment> shipments = new ArrayList<>();
      orderLineItems.forEach(orderLineItem -> {
          Shipment shipment = shipmentClient.getShipmentById(orderLineItem.getShipmentId());
          logger.info("Shipment from feign client: {}", shipment);
          shipment.setOrderLineItems(getOrderLineSummaries(
              orderLineItems
                  .stream()
                  .filter(o -> o.getShipmentId() == shipment.getId())
                  .collect(Collectors.toList()))
          );
          shipments.add(shipment);
      });

      orderDetails.add(new OrderDetail(
          order.getOrderNumber(),
          address,
          order.getTotalPrice(),
          getOrderLineSummaries(orderLineItems),
          shipments
      ));
    });

    return orderDetails;
  }

  private void setPrices(Order order) {
    for (OrderLineItem orderLineItem : order.getOrderLineItems()) {
      logger.debug(orderLineItem.toString());
      setPrice(orderLineItem);
    }
    order.setTotalPrice();
  }

  private void setPrice(OrderLineItem orderLineItem) {
    Product product = productClient.getProductById(orderLineItem.getProductId());
    orderLineItem.setPrice(product.getPrice());
  }

  private List<OrderLineSummary> getOrderLineSummaries(List<OrderLineItem> orderLineItems) {
    List<OrderLineSummary> orderLineSummaries = new ArrayList<>();

    orderLineItems.forEach(orderLineItem -> {
      Product product = productClient.getProductById(orderLineItem.getProductId());
      orderLineSummaries.add(new OrderLineSummary(
          product == null ? "" : product.getName(),
          orderLineItem.getQuantity()
      ));
    });

    return orderLineSummaries;
  }
}
