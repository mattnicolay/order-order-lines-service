package com.solstice.orderorderlines.model;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;

@Entity
@SqlResultSetMapping(name="OrderLineItemFromOrderMapping", classes = {
    @ConstructorResult(targetClass = OrderLineItem.class,
        columns = {
            @ColumnResult(name="product_id", type=Long.class),
            @ColumnResult(name="quantity", type=Integer.class),
            @ColumnResult(name="price", type=Double.class),
            @ColumnResult(name="shipment_id", type=Long.class),
        })
})
@NamedNativeQuery(
    name = "OrderLineItem.findOrderLineItemByIdAndOrderId",
    query = "select product_id, quantity, price, shipment_id "
        + "from order_line_item "
        + "where id = :id and order_id = :orderId",
    resultSetMapping = "OrderLineItemFromOrderMapping"
)
public class OrderLineItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private long productId;
  private int quantity;
  private double price;
  @Transient
  private double totalPrice;
  private long shipmentId;

  public OrderLineItem() {
  }

  public OrderLineItem(long productId, int quantity, double price,
      long shipmentId) {
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
    this.shipmentId = shipmentId;
    setTotalPrice();
  }

  public OrderLineItem(long productId, int quantity, long shipmentId) {
    this.productId = productId;
    this.quantity = quantity;
    this.shipmentId = shipmentId;
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

  public void setPrice(double price) {
    this.price = price;
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

  @Override
  public String toString() {
    return "OrderLineItem{\n" +
        "id=" + id +
        ",\n productId=" + productId +
        ",\n quantity=" + quantity +
        ",\n price=" + price +
        ",\n totalPrice=" + totalPrice +
        ",\n shipmentId=" + shipmentId +
        '}';
  }
}

