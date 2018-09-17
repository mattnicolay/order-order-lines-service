package com.solstice.orderorderlines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;

public class Shipment {
  @JsonProperty(access = Access.WRITE_ONLY)
  private long id;
  @JsonProperty(access = Access.WRITE_ONLY)
  private long accountId;
  @JsonProperty(access = Access.WRITE_ONLY)
  private long shippingAddressId;
  private List<OrderLineSummary> orderLineItems;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime shippedDate;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime deliveryDate;

  public Shipment() {
  }

  public Shipment(long id, long accountId,long shippingAddressId, LocalDateTime shippedDate,
      LocalDateTime deliveryDate) {
    this.id = id;
    this.accountId = accountId;
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
    this.shippingAddressId = shippingAddressId;
  }

  public Shipment(long id, long accountId, long shippingAddressId,
      List<OrderLineSummary> orderLineItems, LocalDateTime shippedDate,
      LocalDateTime deliveryDate) {
    this.id = id;
    this.accountId = accountId;
    this.shippingAddressId = shippingAddressId;
    this.orderLineItems = orderLineItems;
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getAccountId() {
    return accountId;
  }

  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }

  public LocalDateTime getShippedDate() {
    return shippedDate;
  }

  public void setShippedDate(LocalDateTime shippedDate) {
    this.shippedDate = shippedDate;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public long getShippingAddressId() {
    return shippingAddressId;
  }

  public void setShippingAddressId(long shippingAddressId) {
    this.shippingAddressId = shippingAddressId;
  }

  public List<OrderLineSummary> getOrderLineItems() {
    return orderLineItems;
  }

  public void setOrderLineItems(
      List<OrderLineSummary> orderLineItems) {
    this.orderLineItems = orderLineItems;
  }

  public void addOrderLineItem(OrderLineSummary orderLineSummary) {
    orderLineItems.add(orderLineSummary);
  }

  @Override
  public String toString() {
    return "Shipment{" +
        "id=" + id +
        ", accountId=" + accountId +
        ", shippingAddressId=" + shippingAddressId +
        ", orderLineItems=" + orderLineItems +
        ", shippedDate=" + shippedDate +
        ", deliveryDate=" + deliveryDate +
        '}';
  }
}
