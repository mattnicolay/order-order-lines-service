package com.solstice.orderorderlines.external;

import com.solstice.orderorderlines.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product-service")
public interface ProductClient {
  @RequestMapping("/products/{id}")
  Product getProductById(@PathVariable("id") long id);
}
