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
  private Product product;
  private int quantity;
  private double price;
  @Transient
  private double totalPrice;
  @ManyToOne
  @JoinColumn(name = "shipmentId")
  private Shipment shipment;
  @ManyToOne
  @JoinColumn(name = "orderId")
  private Order order;

  public OrderLineItem(){}

  public OrderLineItem(Product product, int quantity, double price,
      Shipment shipment, Order order) {
    this.product = product;
    this.quantity = quantity;
    this.shipment = shipment;
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

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
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
    price = product.getPrice();
    setTotalPrice();
  }

  public double getTotalPrice() {
    setTotalPrice();
    return totalPrice;
  }

  public Shipment getShipment() {
    return shipment;
  }

  public void setShipment(Shipment shipment) {
    this.shipment = shipment;
    this.shipment.addOrderLineItem(this);
  }

  public void setTotalPrice() {
    totalPrice = price*quantity;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
    this.order.addOrderLineItem(this);
  }

  public void removeFromParents() {
    shipment.removeOrderLineItem(this);
    order.removeOrderLineItem(this);
  }
}

