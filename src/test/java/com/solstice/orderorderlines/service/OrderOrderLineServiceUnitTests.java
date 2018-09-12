package com.solstice.orderorderlines.service;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class OrderOrderLineServiceUnitTests {

  private Logger logger = LoggerFactory.getLogger(OrderOrderLineServiceUnitTests.class);

  @InjectMocks
  private OrderOrderLineService orderOrderLineService;

  @Test
  public void getOrdersHasValuesTest() {
    List<Order> orders = orderOrderLineService.getOrders();

    assertThat(orders, is(notNullValue()));
    orders.forEach(order -> {

      assertThat(order, is(notNullValue()));
    });
  }



  private List<Order> getOrders() {
    return Arrays.asList(getOrder1(), getOrder2());
  }

  private List<OrderLineItem> getOrderLineItems() {
    return Arrays.asList(getOrderLineItem1(), getOrderLineItem2(), getOrderLineItem3());
  }

  private Order getOrder1() {
    return new Order(
        1,
        LocalDateTime.of(2018,9,12,2,0),
        1,
        Arrays.asList(getOrderLineItem1(), getOrderLineItem3())
    );
  }

  private Order getOrder2() {
    return new Order(
        1,
        LocalDateTime.of(2018,9,9,5,30),
        2,
        Arrays.asList(getOrderLineItem1(), getOrderLineItem2())
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
