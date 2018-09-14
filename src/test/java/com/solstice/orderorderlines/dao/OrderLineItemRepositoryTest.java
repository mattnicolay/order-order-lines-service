package com.solstice.orderorderlines.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@DatabaseSetup("classpath:test-dataset.xml")
public class OrderLineItemRepositoryTest {

  @Autowired
  private OrderLineItemRepository orderLineItemRepository;

  @Test
  public void findOrderLineItemByIdAndOrderId_ValidId_ReturnsOrderLineItem() {
    OrderLineItem orderLineItem = orderLineItemRepository.findOrderLineItemByIdAndOrderId(1, 1);

    assertThat(orderLineItem, is(notNullValue()));
    assertThat(orderLineItem.getProductId(), is(1L));
    assertThat(orderLineItem.getPrice(), is(2.50));
    assertThat(orderLineItem.getQuantity(), is(2));
    assertThat(orderLineItem.getShipmentId(), is(1L));
  }

  @Test
  public void findOrderLineItemByIdAndOrderId_InvalidId_ReturnsNull() {
    assertThat(orderLineItemRepository.findOrderLineItemByIdAndOrderId(-1, 1),
        is(nullValue()));
  }

  @Test
  public void findOrderLineItemByIdAndOrderId_InvalidOrderId_ReturnsNull() {
    assertThat(orderLineItemRepository.findOrderLineItemByIdAndOrderId(1, -1),
        is(nullValue()));
  }

  @Test
  public void findAllByShipmentId_ValidId_ReturnsListOfOrderLineItems() {
    List<OrderLineItem> orderLineItems = orderLineItemRepository.findAllByShipmentId(1);

    assertThat(orderLineItems, is(notNullValue()));
    assertThat(orderLineItems.size(), is(2));
    orderLineItems.forEach(orderLineItem -> assertThat(orderLineItem, is(notNullValue())));
  }

  @Test
  public void findAllByShipmentId_InvalidId_ReturnsEmptyListOfOrderLineItems() {
    List<OrderLineItem> orderLineItems = orderLineItemRepository.findAllByShipmentId(-1);

    assertThat(orderLineItems, is(notNullValue()));
    assertTrue(orderLineItems.isEmpty());
  }
}