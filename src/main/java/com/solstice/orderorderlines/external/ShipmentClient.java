package com.solstice.orderorderlines.external;

import com.solstice.orderorderlines.model.Shipment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("shipment-service")
public interface ShipmentClient {
  @RequestMapping("/shipments/{id}")
  Shipment getShipmentById(@PathVariable("id") long id);
}
