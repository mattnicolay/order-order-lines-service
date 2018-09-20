package com.solstice.orderorderlines.controller;

import com.solstice.orderorderlines.exception.HTTP400Exception;
import com.solstice.orderorderlines.exception.HTTP404Exception;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderDetail;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.service.OrderOrderLineService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderOrderLineController extends AbstractRestController{

  private OrderOrderLineService orderOrderLineService;

  public OrderOrderLineController(OrderOrderLineService orderOrderLineService) {
    this.orderOrderLineService = orderOrderLineService;
  }

  @GetMapping
  public @ResponseBody List<Order> getOrders(
      @RequestParam(value = "accountId", required = false) Long accountId) {
    List<Order> orders;
    orders = accountId != null ? orderOrderLineService.getOrdersByAccountId(accountId)
        : orderOrderLineService.getOrders();
    if (orders.isEmpty()) {
      throw new HTTP404Exception("Resource not found");
    }
    return orders;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody Order createOrder(@RequestBody Order body) {
    Order order = orderOrderLineService.createOrder(body);
    if (order == null) {
      throw new HTTP400Exception("Could not create order");
    }
    return order;
  }

  @GetMapping("/{accountId}")
  public @ResponseBody List<OrderDetail> getOrderDetails(@PathVariable("accountId") long accountId) {
    List<OrderDetail> orderDetails = orderOrderLineService.getOrderDetails(accountId);
    if (orderDetails.isEmpty()) {
      throw new HTTP404Exception("Resource not found");
    }
    return orderDetails;
  }

  @PutMapping("/{id}")
  public @ResponseBody Order updateOrder(@PathVariable("id") long id, @RequestBody Order body) {
    Order order = orderOrderLineService.updateOrder(id, body);
    return AbstractRestController.checkResourceFound(order);
  }

  @DeleteMapping("/{id}")
  public @ResponseBody Order deleteOrder(@PathVariable("id") long id) {
    Order order = orderOrderLineService.deleteOrder(id);
    return AbstractRestController.checkResourceFound(order);
  }

  @GetMapping("/{id}/lines")
  public @ResponseBody List<OrderLineItem> getOrderLineItems(@PathVariable("id") long id) {
    List<OrderLineItem> orderLineItems = orderOrderLineService.getOrderLineItems(id);
    if(orderLineItems.isEmpty()) {
      throw new HTTP404Exception("Resource not found");
    }
    return orderLineItems;
  }

  @PostMapping("/{id}/lines")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody OrderLineItem createOrderLineItem(
      @PathVariable("id") long id,
      @RequestBody OrderLineItem body) {
    OrderLineItem orderLineItem = orderOrderLineService.createOrderLineItem(id, body);
    if (orderLineItem == null) {
      throw new HTTP400Exception("Could not create order line item");
    }
    return orderLineItem;
  }

  @PutMapping("/{orderId}/lines/{orderLineId}")
  public @ResponseBody OrderLineItem updateOrderLineItem(
      @PathVariable("orderId") long orderId,
      @PathVariable("orderLineId") long orderLineId,
      @RequestBody OrderLineItem body) {
    OrderLineItem orderLineItem = orderOrderLineService.updateOrderLineItem(orderId, orderLineId, body);
    return AbstractRestController.checkResourceFound(orderLineItem);
  }

  @DeleteMapping("/{orderId}/lines/{orderLineId}")
  public @ResponseBody OrderLineItem deleteOrderLineItem(
      @PathVariable("orderId") long orderId,
      @PathVariable("orderLineId") long orderLineId) {
    OrderLineItem orderLineItem = orderOrderLineService.deleteOrderLineItem(orderId, orderLineId);
    return AbstractRestController.checkResourceFound(orderLineItem);
  }
}
