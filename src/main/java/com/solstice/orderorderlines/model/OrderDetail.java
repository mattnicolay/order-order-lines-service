package com.solstice.orderorderlines.model;

import java.util.List;

public class OrderDetail {
  private long orderNumber;
  private Address shippingAddress;
  private double totalPrice;
  private List<OrderLineSummary> orderLineSummaries;
  private List<Shipment> shipments;

  public OrderDetail() {

  }

  public OrderDetail(long orderNumber, Address shippingAddress, double totalPrice,
      List<OrderLineSummary> orderLineSummaries, List<Shipment> shipments) {
    this.orderNumber = orderNumber;
    this.shippingAddress = shippingAddress;
    this.totalPrice = totalPrice;
    this.orderLineSummaries = orderLineSummaries;
    this.shipments = shipments;
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public List<OrderLineSummary> getOrderLineSummaries() {
    return orderLineSummaries;
  }

  public void setOrderLineSummaries(List<OrderLineSummary> orderLineSummaries) {
    this.orderLineSummaries = orderLineSummaries;
  }

  public List<Shipment> getShipments() {
    return shipments;
  }

  public void setShipments(List<Shipment> shipments) {
    this.shipments = shipments;
  }
}
