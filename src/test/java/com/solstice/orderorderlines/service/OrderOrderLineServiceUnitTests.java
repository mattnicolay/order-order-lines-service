package com.solstice.orderorderlines.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.dao.OrderLineItemRepository;
import com.solstice.orderorderlines.dao.OrderRepository;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class OrderOrderLineServiceUnitTests {

  private Logger logger = LoggerFactory.getLogger(OrderOrderLineServiceUnitTests.class);

  @MockBean
  private OrderRepository orderRepository;

  @MockBean
  private OrderLineItemRepository orderLineItemRepository;

  private OrderOrderLineService orderOrderLineService;

  @Before
  public void setup() {
    orderOrderLineService = new OrderOrderLineService(orderRepository, orderLineItemRepository);
  }

  @Test
  public void getOrders_OrdersFound_ReturnsListOfOrders() {
    when(orderRepository.findAll()).thenReturn(getOrders());
    List<Order> orders = orderOrderLineService.getOrders();

    assertThat(orders, is(notNullValue()));
    assertFalse(orders.isEmpty());
    orders.forEach(order -> {
      assertThat(order, is(notNullValue()));
      assertThat(order.getAccountId(), is(notNullValue()));
      assertThat(order.getOrderDate(), is(notNullValue()));
      assertThat(order.getShippingAddressId(), is(notNullValue()));
      assertThat(order.getOrderLineItems(), is(notNullValue()));
      assertThat(order.getTotalPrice(), is(notNullValue()));
    });
  }

  @Test
  public void getOrders_OrdersNotFound_ReturnsEmptyListOfOrders() {
    List<Order> orders = orderOrderLineService.getOrders();

    assertThat(orders, is(notNullValue()));
    assertTrue(orders.isEmpty());
  }

  @Test
  public void getOrderById_ValidId_ReturnsOrder() {
    Order order1 = getOrder1();
    when(orderRepository.findByOrderNumber(anyLong())).thenReturn(order1);
    Order order = orderOrderLineService.getOrderById(1L);

    assertThatOrdersAreEqual(order, order1);
  }

  @Test
  public void getOrderById_InvalidId_ReturnsNull() {
    assertThat(orderOrderLineService.getOrderById(2), is(nullValue()));
  }

  @Test
  public void createOrder_ValidJson_ReturnsCreatedOrder() throws IOException {
    Order order1 = getOrder1();
    Order order = orderOrderLineService.createOrder(toJson(order1));
    assertThatOrdersAreEqual(order, order1);
  }

  @Test(expected = IOException.class)
  public void createOrder_InvalidJson_ThrowsIOException() throws IOException {
    orderOrderLineService.createOrder("{wrong)");
  }

  @Test
  public void updateOrder_ValidIdAndJson_ReturnsOrder() throws IOException {
    Order order1 = getOrder1();
    when(orderRepository.findByOrderNumber(1)).thenReturn(order1);
    Order order = orderOrderLineService.updateOrder(1, toJson(order1));

    assertThatOrdersAreEqual(order, order1);
  }

  @Test
  public void updateOrder_InvalidIdAndValidJson_ReturnsNull() throws IOException {
    assertThat(orderOrderLineService.updateOrder(2, toJson(getOrder1())), is(nullValue()));
  }

  @Test(expected = IOException.class)
  public void updateOrder_ValidIdAndInvalidJson_ThrowsIOException() throws IOException {
    orderOrderLineService.updateOrder(1, "{wrong format)");
  }

  @Test
  public void deleteOrder_ValidId_ReturnDeletedOrder() {
    Order order1 = getOrder1();
    when(orderRepository.findByOrderNumber(1)).thenReturn(order1);
    Order order = orderOrderLineService.deleteOrder(1);

    assertThatOrdersAreEqual(order, order1);
  }

  @Test
  public void deleteOrder_InvalidId_ReturnNull() {
    assertThat(orderOrderLineService.deleteOrder(1), is(nullValue()));
  }

  @Test
  public void getOrderLineItems_ValidId_ReturnsListOfOrderLineItems() {
    Order order1 = getOrder1();
    when(orderRepository.findOrderLineItemsByOrderNumber(1)).thenReturn(order1.getOrderLineItems());
    List<OrderLineItem> orderLineItems = orderOrderLineService.getOrderLineItems(1);


    assertThat(orderLineItems, is(notNullValue()));
    assertFalse(orderLineItems.isEmpty());
    orderLineItems.forEach(orderLineItem -> {
      assertThat(orderLineItem.getProductId(), is(notNullValue()));
      assertThat(orderLineItem.getQuantity(), is(notNullValue()));
      assertThat(orderLineItem.getPrice(), is(notNullValue()));
      assertThat(orderLineItem.getShipmentId(), is(notNullValue()));
      assertThat(orderLineItem.getTotalPrice(), is(notNullValue()));
    });
  }

  @Test
  public void getOrderLineItems_InvalidId_ReturnsEmptyList() {
    List<OrderLineItem> orderLineItems = orderOrderLineService.getOrderLineItems(1);


    assertThat(orderLineItems, is(notNullValue()));
    assertTrue(orderLineItems.isEmpty());
  }

  @Test
  public void createOrderLineItem_ValidIdAndJson_ReturnsCreatedOrder() throws IOException {
    OrderLineItem orderLineItem1 = getOrderLineItem1();
    when(orderRepository.findByOrderNumber(1)).thenReturn(getOrder1());
    OrderLineItem orderLineItem = orderOrderLineService.createOrderLineItem(1, toJson(orderLineItem1));

    assertThatOrderLineItemsAreEqual(orderLineItem, orderLineItem1);
  }

  @Test(expected = IOException.class)
  public void createOrderLineItem_InvalidJson_ThrowsIOException() throws IOException {
    when(orderRepository.findByOrderNumber(1)).thenReturn(getOrder1());
    orderOrderLineService.createOrderLineItem(1, "{wrong)");
  }

  @Test
  public void createOrderLineItem_InvalidId_ReturnsNull() throws IOException {
    assertThat(orderOrderLineService.createOrderLineItem(2, toJson(getOrderLineItem1())), is(nullValue()));
  }


  @Test
  public void updateOrderLineItem_ValidIdAndJson_ReturnsOrder() throws IOException {
    OrderLineItem orderLineItem1 = getOrderLineItem1();
    when(orderLineItemRepository.findOrderLineItemByIdAndOrderId(1, 1))
        .thenReturn(orderLineItem1);
    OrderLineItem orderLineItem = orderOrderLineService
        .updateOrderLineItem(1, 1, toJson(orderLineItem1));

    assertThatOrderLineItemsAreEqual(orderLineItem, orderLineItem1);
  }

  @Test
  public void updateOrderLineItem_InvalidIdAndValidJson_ReturnsNull() throws IOException {
    assertThat(orderOrderLineService
        .updateOrderLineItem(3, 2, toJson(getOrderLineItem1())), is(nullValue()));
  }

  @Test(expected = IOException.class)
  public void updateOrderLineItem_ValidIdAndInvalidJson_ThrowsIOException() throws IOException {
    when(orderLineItemRepository.findOrderLineItemByIdAndOrderId(1,1)).thenReturn(getOrderLineItem1());
    orderOrderLineService.updateOrderLineItem(1, 1, "{wrong format)");
  }

  @Test
  public void deleteOrderLineItem_ValidId_ReturnDeletedOrder() {
    OrderLineItem orderLineItem1 = getOrderLineItem1();
    when(orderLineItemRepository.findOrderLineItemByIdAndOrderId(1, 1))
        .thenReturn(orderLineItem1);
    OrderLineItem orderLineItem = orderOrderLineService
        .deleteOrderLineItem(1, 1);

    assertThatOrderLineItemsAreEqual(orderLineItem, orderLineItem1);
  }

  @Test
  public void deleteOrderLineItem_InvalidId_ReturnNull() {
    assertThat(orderOrderLineService .deleteOrderLineItem(1, 1),
        is(nullValue()));
  }

  private String toJson(Object value) {
    String result = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      result = objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessingException thrown: {}", e.toString());
    }
    return result;
  }

  private void assertThatOrdersAreEqual(Order actual, Order expected) {
    assertThat(actual, is(notNullValue()));
    assertThat(actual.getAccountId(), is(notNullValue()));
    assertThat(actual.getAccountId(), is(expected.getAccountId()));
    assertThat(actual.getOrderDate(), is(notNullValue()));
    assertThat(actual.getOrderDate(), is(equalTo(expected.getOrderDate())));
    assertThat(actual.getShippingAddressId(), is(notNullValue()));
    assertThat(actual.getShippingAddressId(), is(expected.getShippingAddressId()));
    assertThat(actual.getOrderLineItems(), is(notNullValue()));
    assertFalse(actual.getOrderLineItems().isEmpty());
    assertThat(actual.getTotalPrice(), is(notNullValue()));
    assertThat(actual.getTotalPrice(), is(expected.getTotalPrice()));
  }

  private void assertThatOrderLineItemsAreEqual(OrderLineItem actual, OrderLineItem expected) {
    assertThat(actual, is(notNullValue()));
    assertThat(actual.getProductId(), is(notNullValue()));
    assertThat(actual.getProductId(), is(expected.getProductId()));
    assertThat(actual.getQuantity(), is(notNullValue()));
    assertThat(actual.getQuantity(), is(equalTo(expected.getQuantity())));
    assertThat(actual.getPrice(), is(notNullValue()));
    assertThat(actual.getPrice(), is(expected.getPrice()));
    assertThat(actual.getShipmentId(), is(notNullValue()));
    assertThat(actual.getShipmentId(), is(expected.getShipmentId()));
    assertThat(actual.getTotalPrice(), is(notNullValue()));
    assertThat(actual.getTotalPrice(), is(expected.getTotalPrice()));
  }

  private List<Order> getOrders() {
    return Arrays.asList(getOrder1(), getOrder2());
  }

  private List<OrderLineItem> getOrderLineItems() {
    return Arrays.asList(getOrderLineItem1(), getOrderLineItem2(), getOrderLineItem3());
  }

  private Order getOrder1() {
    List<OrderLineItem> list = new ArrayList<>();
    list.add(getOrderLineItem1());
    list.add(getOrderLineItem3());
    return new Order(
        1,
        LocalDateTime.of(2018,9,12,2,0),
        1,
        list
    );
  }

  private Order getOrder2() {
    List<OrderLineItem> list = new ArrayList<>();
    list.add(getOrderLineItem1());
    list.add(getOrderLineItem2());
    return new Order(
        1,
        LocalDateTime.of(2018,9,9,5,30),
        2,
        list
    );
  }

  private OrderLineItem getOrderLineItem1() {
    return new OrderLineItem(1,3, 25.00, 1);
  }

  private OrderLineItem getOrderLineItem2() {
    return new OrderLineItem(2,5, 15.00, 2);
  }

  private OrderLineItem getOrderLineItem3() {
    return new OrderLineItem(3,8, 40.00, 3);
  }
}
