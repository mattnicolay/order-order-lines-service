package com.solstice.orderorderlines.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.exception.OrderOrderLineExceptionHandler;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.service.OrderOrderLineService;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    mockMvc = MockMvcBuilders.standaloneSetup(orderOrderLineController)
        .setControllerAdvice(new OrderOrderLineExceptionHandler()).build();
  }


  @Test
  public void getOrdersSuccessTest() {
    when(orderOrderLineService.getOrders()).thenReturn(Arrays.asList(new Order()));
    mockMvcPerform(GET, "/orders", 200, toJson(Arrays.asList(new Order())));
  }

  @Test
  public void getOrdersFailureTest() {
    mockMvcPerform(GET, "/orders", 404, "");
  }

  @Test
  public void postOrderSuccessTest() throws IOException {
    when(orderOrderLineService.createOrder(anyString())).thenReturn(new Order());
    mockMvcPerform(POST, "/orders", toJson(new Order()), 201, toJson(new Order()));
  }

  @Test
  public void postOrderFailureTest() {
    mockMvcPerform(POST, "/orders", toJson(new Order()), 500, "");
  }

  @Test
  public void postOrderEmptyBodyTest() {
    mockMvcPerform(POST, "/orders", 400, "");
  }

  @Test
  public void createOrder_InvalidJson_Code400() throws IOException {
    when(orderOrderLineService.createOrder(anyString())).thenThrow(new IOException());
    mockMvcPerform(POST, "/orders", "{wrong}", 400, "");
  }

  @Test
  public void putOrderSuccessTest() throws IOException {
    when(orderOrderLineService.updateOrder(anyLong(), anyString())).thenReturn(new Order());
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 200, toJson(new Order()));
  }

  @Test
  public void putOrderNotFoundTest() {
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 404, "");
  }

  @Test
  public void putOrderEmptyBodyTest() {
    mockMvcPerform(PUT, "/orders/1", 400, "");
  }

  @Test
  public void updateOrder_InvalidJson_Code400() throws IOException {
    when(orderOrderLineService.updateOrder(anyLong(), anyString())).thenThrow(new IOException());
    mockMvcPerform(PUT, "/orders/1", "{wrong}", 400, "");
  }

  @Test
  public void deleteOrderSuccessTest() {
    when(orderOrderLineService.deleteOrder(anyLong())).thenReturn(new Order());
    mockMvcPerform(DELETE, "/orders/1", 200, toJson(new Order()));
  }

  @Test
  public void deleteOrderNotFoundTest() {
    mockMvcPerform(DELETE, "/orders/1", 404, "");
  }

  @Test
  public void getOrderLinesSuccessTest() {
    when(orderOrderLineService.getOrderLineItems(anyLong())).thenReturn(Arrays.asList(new OrderLineItem()));
    mockMvcPerform(GET, "/orders/1/lines", 200, toJson(Arrays.asList(new OrderLineItem())));
  }

  @Test
  public void getOrderLinesNotFoundTest() {
    mockMvcPerform(GET, "/orders/1/lines", 404, "");
  }

  @Test
  public void postOrderLineSuccessTest() throws IOException {
    when(orderOrderLineService.createOrderLineItem(anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 201, toJson(new OrderLineItem()));
  }

  @Test
  public void postOrderLineFailureTest() {
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 500, "");
  }

  @Test
  public void postOrderLineEmptyBodyTest() {
    mockMvcPerform(POST, "/orders/1/lines", 400, "");
  }

  @Test
  public void createOrderLine_InvalidJson_Code400() throws IOException {
    when(orderOrderLineService.createOrderLineItem(1, "{wrong}")).thenThrow(new IOException());
    mockMvcPerform(POST, "/orders/1/lines", "{wrong}", 400, "");
  }

  @Test
  public void putOrderLineSuccessTest() throws IOException {
    when(orderOrderLineService.updateOrderLineItem(anyLong(), anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 200, toJson(new OrderLineItem()));
  }

  @Test
  public void putOrderLineNotFoundTest() {
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 404,"");
  }

  @Test
  public void putOrderLineEmptyBodyTest() {
    mockMvcPerform(PUT, "/orders/1/lines/1", 400, "");
  }

  @Test
  public void updateOrderLine_InvalidJson_Code400() throws IOException {
    when(orderOrderLineService.updateOrderLineItem(1,1, "{wrong}")).thenThrow(new IOException());
    mockMvcPerform(PUT, "/orders/1/lines/1", "{wrong}", 400, "");
  }

  @Test
  public void deleteOrderLineSuccessTest() {
    when(orderOrderLineService.deleteOrderLineItem(anyLong(), anyLong())).thenReturn(new OrderLineItem());
    mockMvcPerform(DELETE, "/orders/1/lines/1", 200, toJson(new OrderLineItem()));
  }

  @Test
  public void deleteOrderLineNotFoundTest() {
    mockMvcPerform(DELETE, "/orders/1/lines/1", 404,"");
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

  private void mockMvcPerform(String method, String endpoint, int expectedStatus,
      String expectedResponseBody) {
    mockMvcPerform(method, endpoint, "", expectedStatus, expectedResponseBody);
  }

  private void mockMvcPerform(String method, String endpoint, String requestBody, int expectedStatus,
      String expectedResponseBody) {
    try {
      switch(method){

        case GET:
          mockMvc.perform(get(endpoint)).andExpect(status().is(expectedStatus))
          .andExpect(content().json(expectedResponseBody));
          break;

        case POST:
          mockMvc.perform(
              post(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus))
              .andExpect(content().json(expectedResponseBody));
          break;

        case PUT:
          mockMvc.perform(
              put(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus))
              .andExpect(content().json(expectedResponseBody));
          break;

        case DELETE:
          mockMvc.perform(delete(endpoint)).andExpect(status().is(expectedStatus))
              .andExpect(content().json(expectedResponseBody));
          break;

        default:
          logger.error("Unknown method '{}' given to mockMvcPerform", method);
      }
    } catch (Exception e) {
      logger.error("Exception thrown: {}", e.toString());
    }
  }
}
