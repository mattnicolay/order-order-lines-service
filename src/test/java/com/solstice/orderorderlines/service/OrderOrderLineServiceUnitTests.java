package com.solstice.orderorderlines.service;

import static junit.framework.TestCase.fail;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityNotFoundException;
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
  @MockBean
  private AccountAddressClient accountAddressClient;
  @MockBean
  private ProductClient productClient;
  @MockBean
  private ShipmentClient shipmentClient;

  private OrderOrderLineService orderOrderLineService;

  @Before
  public void setup() {
    orderOrderLineService = new OrderOrderLineService(
        orderLineItemRepository,
        orderRepository,
        accountAddressClient,
        productClient,
        shipmentClient);
  }

  @Test
  public void getOrders_OrdersFound_ReturnsListOfOrders() {
    when(orderRepository.findAll()).thenReturn(getOrders());
    List<Order> orders = orderOrderLineService.getOrders();

    assertThat(orders, is(notNullValue()));
    assertFalse(orders.isEmpty());
    assertThatOrderListElementsAreNotEmpty(orders);
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

  @Test
  public void getOrdersByAccountId_ValidId_ReturnsListOfOrders() {
    when(orderRepository.findAllByAccountIdOrderByOrderDate(1)).thenReturn(getOrders());
    List<Order> orders = orderOrderLineService.getOrdersByAccountId(1);

    assertThat(orders, is(notNullValue()));
    assertFalse(orders.isEmpty());
    assertThatOrderListElementsAreNotEmpty(orders);
  }

  @Test
  public void getOrdersByAccountId_InvalidId_ReturnsEmptyListOfOrders() {
    List<Order> orders = orderOrderLineService.getOrdersByAccountId(-1);

    assertThat(orders, is(notNullValue()));
    assertTrue(orders.isEmpty());
  }

  @Test
  public void getOrderDetails_ValidId_ReturnsListOfOrderDetail() {
    Order order1 = getOrder1();
    order1.setOrderNumber(1L);
    Order order2 = getOrder2();
    order2.setOrderNumber(2L);

    Address address = new Address(
        "111 N Canal St",
        "700",
        "Chicago",
        "IL",
        "60606",
        "United States"
    );

    Product product = new Product("test");

    Shipment testShipment = new Shipment(
        1,
        1,
        1,
        getOrderLineSummaries(getOrderLineItems()),
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));

    when(productClient.getProductById(anyLong())).thenReturn(product);
    when(orderRepository.findAllByAccountIdOrderByOrderDate(1)).thenReturn(Arrays.asList(
        order1,
        order2
    ));
    when(orderRepository.findOrderLineItemsByOrderNumber(anyLong())).thenReturn(Arrays.asList(
        getOrderLineItem1(),
        getOrderLineItem2()
    ));
    when(accountAddressClient.getAddressByAccountIdAndAddressId(anyLong(), anyLong()))
        .thenReturn(address);
    when(shipmentClient.getShipmentById(anyLong())).thenReturn(testShipment);

    List<OrderDetail> orderDetails = orderOrderLineService.getOrderDetails(1);

    assertThat(orderDetails, is(notNullValue()));
    assertThat(orderDetails.size(), is(2));
    orderDetails.forEach(orderDetail -> {
      assertThat(orderDetail, is(notNullValue()));
    });

    OrderDetail orderDetail = orderDetails.get(0);
    assertThat(orderDetail.getOrderNumber(), is(1L));
    assertThat(orderDetail.getShippingAddress(), is(equalTo(address)));
    assertThat(orderDetail.getTotalPrice(), is(getOrder1().getTotalPrice()));

    orderDetail.getOrderLineItems().forEach(orderLineSummary -> {
      assertThat(orderLineSummary, is(notNullValue()));
      assertThat(orderLineSummary.getProductName(), is(equalTo(product.getName())));
      assertThat(orderLineSummary.getQuantity(), is(3));
    });

    orderDetail.getShipments().forEach(shipment -> {
      assertThat(shipment, is(notNullValue()));
      assertThat(shipment.getOrderLineItems(), is(
          equalTo(testShipment.getOrderLineItems())));
      assertThat(shipment.getShippedDate(), is(equalTo(testShipment.getShippedDate())));
      assertThat(shipment.getDeliveryDate(), is(equalTo(testShipment.getDeliveryDate())));
    });
  }


  @Test
  public void getOrderDetails_InvalidId_ReturnsEmptyListOfOrderDetails() {
    List<OrderDetail> orderDetails = orderOrderLineService.getOrderDetails(1);

    assertThat(orderDetails, is(notNullValue()));
    assertTrue(orderDetails.isEmpty());
  }

  @Test(expected = EntityNotFoundException.class)
  public void getOrderDetails_AccountAddressServiceIsDown_ThrowsEntityNotFoundException() {

    Order order1 = getOrder1();
    order1.setOrderNumber(1L);
    Order order2 = getOrder2();
    order2.setOrderNumber(2L);

    when(orderRepository.findAllByAccountIdOrderByOrderDate(1)).thenReturn(Arrays.asList(
        order1,
        order2
    ));

    orderOrderLineService.getOrderDetails(1);
  }

  @Test(expected = EntityNotFoundException.class)
  public void getOrderDetails_ShipmentServiceIsDown_ThrowsEntityNotFoundException() {

    Order order1 = getOrder1();
    order1.setOrderNumber(1L);
    Order order2 = getOrder2();
    order2.setOrderNumber(2L);

    Address address = new Address(
        "111 N Canal St",
        "700",
        "Chicago",
        "IL",
        "60606",
        "United States"
    );

    Product product = new Product("test");

    when(orderRepository.findAllByAccountIdOrderByOrderDate(1)).thenReturn(Arrays.asList(
        order1,
        order2
    ));
    when(orderRepository.findOrderLineItemsByOrderNumber(anyLong())).thenReturn(Arrays.asList(
        getOrderLineItem1(),
        getOrderLineItem2()
    ));
    when(accountAddressClient.getAddressByAccountIdAndAddressId(anyLong(), anyLong()))
        .thenReturn(address);

    orderOrderLineService.getOrderDetails(1);
  }

  @Test(expected = EntityNotFoundException.class)
  public void getOrderDetails_ProductServiceIsDown_ThrowsEntityNotFoundException() {

    Order order1 = getOrder1();
    order1.setOrderNumber(1L);
    Order order2 = getOrder2();
    order2.setOrderNumber(2L);

    Address address = new Address(
        "111 N Canal St",
        "700",
        "Chicago",
        "IL",
        "60606",
        "United States"
    );

    Shipment testShipment = new Shipment(
        1,
        1,
        1,
        getOrderLineSummaries(getOrderLineItems()),
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));

    when(orderRepository.findAllByAccountIdOrderByOrderDate(1)).thenReturn(Arrays.asList(
        order1,
        order2
    ));
    when(orderRepository.findOrderLineItemsByOrderNumber(anyLong())).thenReturn(Arrays.asList(
        getOrderLineItem1(),
        getOrderLineItem2()
    ));
    when(accountAddressClient.getAddressByAccountIdAndAddressId(anyLong(), anyLong()))
        .thenReturn(address);
    when(shipmentClient.getShipmentById(anyLong())).thenReturn(testShipment);

    orderOrderLineService.getOrderDetails(1);
  }

  @Test
   public void testProductFromJson() {
    ObjectMapper objectMapper = new ObjectMapper();
    Product product = null;
    try {
      product = objectMapper.readValue(
          "{\"id\":0,"
              + "\"name\":\"Test\","
          + "\"description\":\"Test\","
          + "\"image\":\"TestImage\","
          + "\"price\":1.5}", Product.class);
    } catch (IOException e) {
      logger.error(e.toString());
      fail();
    }
    assertThat(product, is(notNullValue()));
    assertThat(product.getName(), is(equalTo("Test")));
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


  private void assertThatOrderListElementsAreNotEmpty(List<Order> orders) {
    orders.forEach(order -> {
      assertThat(order, is(notNullValue()));
      assertThat(order.getAccountId(), is(notNullValue()));
      assertThat(order.getOrderDate(), is(notNullValue()));
      assertThat(order.getShippingAddressId(), is(notNullValue()));
      assertThat(order.getOrderLineItems(), is(notNullValue()));
      assertThat(order.getTotalPrice(), is(notNullValue()));
    });
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
    return new OrderLineItem(2,3, 15.00, 1);
  }

  private OrderLineItem getOrderLineItem3() {
    return new OrderLineItem(3,8, 40.00, 2);
  }

  private List<OrderLineSummary> getOrderLineSummaries(List<OrderLineItem> orderLineItems) {
    List<OrderLineSummary> orderLineSummaries = new ArrayList<>();

    orderLineItems.forEach(orderLineItem ->
        orderLineSummaries.add(new OrderLineSummary(
            "test",
            orderLineItem.getQuantity()
        )));

    return orderLineSummaries;
  }
}
