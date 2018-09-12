package com.solstice.orderorderlines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long orderNumber;
  private long accountId;
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime orderDate;
  private long shippingAddressId;
  @OneToMany
  @JoinColumn(name = "orderId")
  @JsonIgnoreProperties("orderId")
  private List<OrderLineItem> orderLineItems;
  @Transient
  private double totalPrice;

  public Order(){}

  public Order(long accountId, LocalDateTime orderDate, long shippingAddressId,
      List<OrderLineItem> orderLineItems) {
    this.accountId = accountId;
    this.orderDate = orderDate;
    this.shippingAddressId = shippingAddressId;
    this.orderLineItems = orderLineItems;
    setTotalPrice();
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public long getAccountId() {
    return accountId;
  }

  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public long getShippingAddressId() {
    return shippingAddressId;
  }

  public void setShippingAddressId(long shippingAddressId) {
    this.shippingAddressId = shippingAddressId;
  }

  public List<OrderLineItem> getOrderLineItems() {
    return orderLineItems;
  }

  public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
    this.orderLineItems = orderLineItems;
    setTotalPrice();
  }

  public void addOrderLineItem(OrderLineItem orderLineItem) {
    orderLineItems.add(orderLineItem);
    setTotalPrice();
  }

  public void removeOrderLineItem(OrderLineItem orderLineItem) {
    orderLineItems.remove(orderLineItem);
    setTotalPrice();
  }

  public double getTotalPrice() {
    setTotalPrice();
    return totalPrice;
  }

  private void setTotalPrice() {
    totalPrice = 0;
    if (orderLineItems != null) {
      this.orderLineItems.forEach(o -> totalPrice += o.getTotalPrice());
    }
  }
}

