package com.solstice.orderorderlines.controller;

import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.service.OrderOrderLineService;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Path;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderOrderLineController {

  private OrderOrderLineService orderOrderLineService;

  public OrderOrderLineController(OrderOrderLineService orderOrderLineService) {
    this.orderOrderLineService = orderOrderLineService;
  }

  @GetMapping
  public ResponseEntity<List<Order>> getOrders() {
    List<Order> orders = orderOrderLineService.getOrders();
    return new ResponseEntity<>(
        orders,
        new HttpHeaders(),
        orders.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody String body) {
    Order order = orderOrderLineService.createOrder(body);
    return new ResponseEntity<>(
        order,
        new HttpHeaders(),
        order == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.CREATED
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<Order> updateOrder(@PathVariable("id") long id, @RequestBody String body) {
    Order order = orderOrderLineService.updateOrder(id, body);
    return new ResponseEntity<>(
        order,
        new HttpHeaders(),
        order == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Order> deleteOrder(@PathVariable("id") long id) {
    Order order = orderOrderLineService.deleteOrder(id);
    return new ResponseEntity<>(
        order,
        new HttpHeaders(),
        order == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @GetMapping("/{id}/lines")
  public ResponseEntity<List<OrderLineItem>> getOrderLineItems(@PathVariable("id") long id) {
    List<OrderLineItem> orderLineItems = orderOrderLineService.getOrderLineItems(id);
    return new ResponseEntity<>(
        orderLineItems,
        new HttpHeaders(),
        orderLineItems.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @PostMapping("/{id}/lines")
  public ResponseEntity<OrderLineItem> createOrderLineItem(@PathVariable("id") long id, @RequestBody String body) {
    OrderLineItem orderLineItem = orderOrderLineService.createOrderLineItem(id, body);
    return new ResponseEntity<>(
        orderLineItem,
        new HttpHeaders(),
        orderLineItem == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.CREATED
    );
  }

  @PutMapping("/{orderId}/lines/{orderLineId}")
  public ResponseEntity<OrderLineItem> updateOrderLineItem(
      @PathVariable("orderId") long orderId,
      @PathVariable("orderLineId") long orderLineId,
      @RequestBody String body) {
    OrderLineItem orderLineItem = orderOrderLineService.updateOrderLineItem(orderId, orderLineId, body);
    return new ResponseEntity<>(
        orderLineItem,
        new HttpHeaders(),
        orderLineItem == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @DeleteMapping("/{orderId}/lines/{orderLineId}")
  public ResponseEntity<OrderLineItem> deleteOrderLineItem(
      @PathVariable("orderId") long orderId,
      @PathVariable("orderLineId") long orderLineId) {
    OrderLineItem orderLineItem = orderOrderLineService.deleteOrderLineItem(orderId, orderLineId);
    return new ResponseEntity<>(
        orderLineItem,
        new HttpHeaders(),
        orderLineItem == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }
}
