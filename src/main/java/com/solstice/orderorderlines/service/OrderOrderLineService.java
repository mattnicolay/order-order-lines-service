package com.solstice.orderorderlines.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderOrderLineService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private OrderLineItemRepository orderLineItemRepository;
  private OrderRepository orderRepository;
  private AccountAddressClient accountAddressClient;
  private ProductClient productClient;
  private ShipmentClient shipmentClient;

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
  }

  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  public Order getOrderById(long id) {
    return orderRepository.findByOrderNumber(id);
  }

  public Order createOrder(Order order) {
    setPrices(order);
    orderRepository.save(order);
    return order;
  }

  public Order updateOrder(long id, Order orderToUpdate){
    if(orderToUpdate == null || orderRepository.findByOrderNumber(id) == null) {
      return null;
    }
    setPrices(orderToUpdate);
    orderToUpdate.setOrderNumber(id);
    orderRepository.save(orderToUpdate);
    return orderToUpdate;
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

  public OrderLineItem createOrderLineItem(long id, OrderLineItem orderLineItem) {
    Order order = orderRepository.findByOrderNumber(id);
    if (orderLineItem == null || order == null) {
      return null;
    }
    setPrice(orderLineItem);
    order.addOrderLineItem(orderLineItem);
    orderRepository.save(order);

    return orderLineItem;
  }

  public OrderLineItem updateOrderLineItem(long orderId, long orderLineId, OrderLineItem orderLineItem) {
    OrderLineItem dbOrderLineItem = orderLineItemRepository
        .findOrderLineItemByIdAndOrderId(orderLineId, orderId);
    if (orderLineItem == null || dbOrderLineItem == null) {
      return null;
    }
    setPrice(orderLineItem);
    orderLineItem.setId(orderId);
    orderLineItemRepository.save(orderLineItem);

    return orderLineItem;
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

    for (Order order : orders) {
      Address address = accountAddressClient.getAddressByAccountIdAndAddressId(
          order.getAccountId(), order.getShippingAddressId());
      logger.info("Address from feign client: {}", address);
      List<OrderLineItem> orderLineItems = orderRepository
          .findOrderLineItemsByOrderNumber(order.getOrderNumber());

      List<Shipment> shipments = getShipmentsForOrderLineItems(orderLineItems);

      orderDetails.add(new OrderDetail(
          order.getOrderNumber(),
          address,
          order.getTotalPrice(),
          getOrderLineSummaries(orderLineItems),
          shipments
      ));
    }

    return orderDetails;
  }

  @HystrixCommand(fallbackMethod = "getShipmentsFallback")
  private List<Shipment> getShipmentsForOrderLineItems(List<OrderLineItem> orderLineItems) {
    List<Shipment> shipments = new ArrayList<>();
    for (OrderLineItem orderLineItem : orderLineItems) {
      Shipment shipment = shipmentClient.getShipmentById(orderLineItem.getShipmentId());
      logger.info("Shipment from feign client: {}", shipment);
      shipment.setOrderLineItems(getOrderLineSummaries(
          orderLineItems
              .stream()
              .filter(o -> o.getShipmentId() == shipment.getId())
              .collect(Collectors.toList()))
      );
      shipments.add(shipment);
    }
    return shipments;
  }

  private List<Shipment> getShipmentsFallback(List<OrderLineItem> orderLineItems) {
    return new ArrayList<>();
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
