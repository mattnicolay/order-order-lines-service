package com.solstice.orderorderlines.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.service.OrderOrderLineService;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(OrderOrderLineController.class)
public class OrderOrderLineControllerUnitTests {

  private Logger logger = LoggerFactory.getLogger(OrderOrderLineControllerUnitTests.class);
  private final String GET = "GET";
  private final String POST = "POST";
  private final String PUT = "PUT";
  private final String DELETE = "DELETE";

  @Mock
  private OrderOrderLineService orderOrderLineService;

  @InjectMocks
  private OrderOrderLineController orderOrderLineController;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(orderOrderLineController).build();
  }


  @Test
  public void getOrdersSuccessTest() {
    when(orderOrderLineService.getOrders()).thenReturn(Arrays.asList(new Order()));
    mockMvcPerform(GET, "/orders", 200);
  }

  @Test
  public void getOrdersFailureTest() {
    mockMvcPerform(GET, "/orders", 404);
  }

  @Test
  public void postOrderSuccessTest() {
    when(orderOrderLineService.createOrder(anyString())).thenReturn(new Order());
    mockMvcPerform(POST, "/orders", toJson(new Order()), 201);
  }

  @Test
  public void postOrderFailureTest() {
    mockMvcPerform(POST, "/orders", toJson(new Order()), 500);
  }

  @Test
  public void postOrderEmptyBodyTest() {
    mockMvcPerform(POST, "/orders", 400);
  }

  @Test
  public void putOrderSuccessTest() {
    when(orderOrderLineService.updateOrder(anyLong(), anyString())).thenReturn(new Order());
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 200);
  }

  @Test
  public void putOrderNotFoundTest() {
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 404);
  }

  @Test
  public void putOrderEmptyBodyTest() {
    mockMvcPerform(PUT, "/orders/1", 400);
  }

  @Test
  public void deleteOrderSuccessTest() {
    when(orderOrderLineService.deleteOrder(anyLong())).thenReturn(new Order());
    mockMvcPerform(DELETE, "/orders/1",200);
  }

  @Test
  public void deleteOrderNotFoundTest() {
    mockMvcPerform(DELETE, "/orders/1", 404);
  }

  @Test
  public void getOrderLinesSuccessTest() {
    when(orderOrderLineService.getOrderLineItems(anyLong())).thenReturn(Arrays.asList(new OrderLineItem()));
    mockMvcPerform(GET, "/orders/1/lines", 200);
  }

  @Test
  public void getOrderLinesNotFoundTest() {
    mockMvcPerform(GET, "/orders/1/lines", 404);
  }

  @Test
  public void postOrderLineSuccessTest() {
    when(orderOrderLineService.createOrderLineItem(anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 201);
  }

  @Test
  public void postOrderLineFailureTest() {
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 500);
  }

  @Test
  public void postOrderLineEmptyBodyTest() {
    mockMvcPerform(POST, "/orders/1/lines", 400);
  }

  @Test
  public void putOrderLineSuccessTest() {
    when(orderOrderLineService.updateOrderLineItem(anyLong(), anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 200);
  }

  @Test
  public void putOrderLineNotFoundTest() {
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 404);
  }

  @Test
  public void putOrderLineEmptyBodyTest() {
    mockMvcPerform(PUT, "/orders/1/lines/1", 400);
  }

  @Test
  public void deleteOrderLineSuccessTest() {
    when(orderOrderLineService.deleteOrderLineItem(anyLong(), anyLong())).thenReturn(new OrderLineItem());
    mockMvcPerform(DELETE, "/orders/1/lines/1", 200);
  }

  @Test
  public void deleteOrderLineNotFoundTest() {
    mockMvcPerform(DELETE, "/orders/1/lines/1", 404);
  }

  private String toJson(Object value) {
    String result = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      result = objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessingException thrown: {}", e.toString());
    }
    return result;
  }

  private void mockMvcPerform(String method, String endpoint, int expectedStatus) {
    mockMvcPerform(method, endpoint, "", expectedStatus);
  }

  private void mockMvcPerform(String method, String endpoint, String requestBody, int expectedStatus) {
    try {
      switch(method){

        case GET:
          mockMvc.perform(get(endpoint)).andExpect(status().is(expectedStatus));
          break;

        case POST:
          mockMvc.perform(
              post(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus));
          break;

        case PUT:
          mockMvc.perform(
              put(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus));
          break;

        case DELETE:
          mockMvc.perform(delete(endpoint)).andExpect(status().is(expectedStatus));
          break;

        default:
          logger.error("Unknown method '{}' given to mockMvcPerform", method);
      }
    } catch (Exception e) {
      logger.error("Exception thrown: {}", e.toString());
    }
  }
}
