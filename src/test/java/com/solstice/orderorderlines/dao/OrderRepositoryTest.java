package com.solstice.orderorderlines.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@DatabaseSetup("classpath:test-dataset.xml")
public class OrderRepositoryTest {

  @Autowired
  private OrderRepository orderRepository;

  @Test
  public void findOrderLineItemsById_ValidId_ReturnsOrderLineItemsList() {
    List<OrderLineItem> orderLineItems = orderRepository.findOrderLineItemsByOrderNumber(1);

    assertThat(orderLineItems, is(notNullValue()));
    assertFalse(orderLineItems.isEmpty());
    orderLineItems.forEach(orderLineItem -> {
      assertThat(orderLineItem, is(notNullValue()));
      assertThat(orderLineItem.getId(), is(notNullValue()));
      assertThat(orderLineItem.getProductId(), is(notNullValue()));
      assertThat(orderLineItem.getQuantity(), is(notNullValue()));
      assertThat(orderLineItem.getPrice(), is(notNullValue()));
      assertThat(orderLineItem.getTotalPrice(), is(notNullValue()));
      assertThat(orderLineItem.getShipmentId(), is(notNullValue()));
    });
  }

  @Test
  public void findOrderLineItemsById_InvalidId_ReturnsEmptyOrderLineItemsList() {
    List<OrderLineItem> orderLineItems = orderRepository.findOrderLineItemsByOrderNumber(-1);

    assertThat(orderLineItems, is(notNullValue()));
    assertTrue(orderLineItems.isEmpty());
  }

  @Test
  public void findAllByAccountIdOrderByOrderDate_ValidId_ReturnsListOfOrdersOrderedByOrderDate() {
    List<Order> orders = orderRepository.findAllByAccountIdOrderByOrderDate(1);

    assertThat(orders, is(notNullValue()));
    assertThat(orders.size(), is(2));
    assertThat(orders.get(0).getOrderNumber(), is(2L));
  }

  @Test
  public void findAllByAccountIdOrderByOrderDate_InvalidId_ReturnsEmptyListOfOrders() {
    List<Order> orders = orderRepository.findAllByAccountIdOrderByOrderDate(-1);

    assertThat(orders, is(notNullValue()));
    assertTrue(orders.isEmpty());
  }
}