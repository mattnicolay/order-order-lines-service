package com.solstice.orderorderlines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SolrAutoConfiguration.class})
@EnableFeignClients
@EnableDiscoveryClient
@EnableHystrix
public class OrderOrderLinesServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderOrderLinesServiceApplication.class, args);
  }
}
