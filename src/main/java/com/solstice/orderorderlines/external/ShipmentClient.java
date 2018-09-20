package com.solstice.orderorderlines.external;

import com.solstice.orderorderlines.model.Shipment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "shipment-service")
public interface ShipmentClient {
  @RequestMapping("/shipments/{id}")
  Shipment getShipmentById(@PathVariable("id") long id);
}


//class ShipmentClientFallback implements ShipmentClient{
//  @Override
//  public Shipment getShipmentById(long id) {
//    return new Shipment();
//  }
//}
