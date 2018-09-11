package com.solstice.orderorderlines.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class OrderLineItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @ManyToOne
  @JoinColumn(name = "productId")
  private long productId;
  private int quantity;
  private double price;
  @Transient
  private double totalPrice;
  @ManyToOne
  @JoinColumn(name = "shipmentId")
  private long shipmentId;
  @ManyToOne
  @JoinColumn(name = "orderId")
  private Order order;

  public OrderLineItem() {
  }

  public OrderLineItem(long productId, int quantity, double price,
      long shipmentId, Order order) {
    this.productId = productId;
    this.quantity = quantity;
    this.shipmentId = shipmentId;
    this.order = order;
    setPrice();
    setTotalPrice();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
    setPrice();
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    setTotalPrice();
  }

  public double getPrice() {
    return price;
  }

  public void setPrice() {
    setTotalPrice();
  }

  public double getTotalPrice() {
    setTotalPrice();
    return totalPrice;
  }

  public long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public void setTotalPrice() {
    totalPrice = price * quantity;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
    this.order.addOrderLineItem(this);
  }
}

