package com.solstice.orderorderlines.controller;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.orderorderlines.exception.OrderOrderLineExceptionHandler;
import com.solstice.orderorderlines.model.Address;
import com.solstice.orderorderlines.model.Order;
import com.solstice.orderorderlines.model.OrderDetail;
import com.solstice.orderorderlines.model.OrderLineItem;
import com.solstice.orderorderlines.model.OrderLineSummary;
import com.solstice.orderorderlines.model.Product;
import com.solstice.orderorderlines.model.Shipment;
import com.solstice.orderorderlines.service.OrderOrderLineService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private Logger logger = LoggerFactory.getLogger(this.getClass());
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
  public void getOrdersSuccessTest() throws Exception {
    when(orderOrderLineService.getOrders()).thenReturn(getOrders());
    mockMvcPerform(GET, "/orders", 200, toJson(getOrders()));
  }

  @Test
  public void getOrdersFailureTest() throws Exception {
    mockMvcPerform(GET, "/orders", 404, "[]");
  }

  @Test
  public void getOrdersByAccountId_ValidId_Code200ReturnsListOfOrders() throws Exception {
    when(orderOrderLineService.getOrdersByAccountId(1)).thenReturn(getOrders());
    mockMvcPerform(GET, "/orders?accountId=1", 200, toJson(getOrders()));
  }

  @Test
  public void getOrdersByAccountId_InvalidId_Code404EmptyResponse() throws Exception {
    mockMvcPerform(GET, "/orders?accountId=-1", 404, "[]");
  }

  @Test
  public void getOrderDetails_ValidId_Code200ReturnsListOfOrderDetails() throws Exception {
    when(orderOrderLineService.getOrderDetails(1)).thenReturn(getOrderDetails());
    mockMvcPerform(GET, "/orders/1", 200, toJson(getOrderDetails()));
  }

  @Test
  public void getOrderDetails_InvalidId_Code404EmptyResponse() throws Exception {
    mockMvcPerform(GET, "/orders/-1", 404, "[]");
  }


  @Test
  public void postOrderSuccessTest() throws Exception {
    when(orderOrderLineService.createOrder(anyString())).thenReturn(new Order());
    mockMvcPerform(POST, "/orders", toJson(new Order()), 201, toJson(new Order()));
  }

  @Test
  public void postOrderFailureTest() throws Exception {
    mockMvcPerform(POST, "/orders", toJson(new Order()), 500, "");
  }

  @Test
  public void postOrderEmptyBodyTest() throws Exception {
    mockMvcPerform(POST, "/orders", 400, "");
  }

  @Test
  public void createOrder_InvalidJson_Code400() throws Exception {
    when(orderOrderLineService.createOrder(anyString())).thenThrow(new IOException());
    mockMvcPerform(POST, "/orders", "{wrong}", 400, "<h1>ERROR:</h1>\n"
        + " Invalid Json format");
  }

  @Test
  public void putOrderSuccessTest() throws Exception {
    when(orderOrderLineService.updateOrder(anyLong(), anyString())).thenReturn(new Order());
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 200, toJson(new Order()));
  }

  @Test
  public void putOrderNotFoundTest() throws Exception {
    mockMvcPerform(PUT, "/orders/1", toJson(new Order()), 404, "");
  }

  @Test
  public void putOrderEmptyBodyTest() throws Exception {
    mockMvcPerform(PUT, "/orders/1", 400, "");
  }

  @Test
  public void updateOrder_InvalidJson_Code400() throws Exception {
    when(orderOrderLineService.updateOrder(anyLong(), anyString())).thenThrow(new IOException());
    mockMvcPerform(PUT, "/orders/1", "{wrong}", 400, "<h1>ERROR:</h1>\n"
        + " Invalid Json format");
  }

  @Test
  public void deleteOrderSuccessTest() throws Exception {
    when(orderOrderLineService.deleteOrder(anyLong())).thenReturn(new Order());
    mockMvcPerform(DELETE, "/orders/1", 200, toJson(new Order()));
  }

  @Test
  public void deleteOrderNotFoundTest() throws Exception {
    mockMvcPerform(DELETE, "/orders/1", 404, "");
  }

  @Test
  public void getOrderLinesSuccessTest() throws Exception {
    when(orderOrderLineService.getOrderLineItems(anyLong())).thenReturn(Arrays.asList(new OrderLineItem()));
    mockMvcPerform(GET, "/orders/1/lines", 200, toJson(Arrays.asList(new OrderLineItem())));
  }

  @Test
  public void getOrderLinesNotFoundTest() throws Exception {
    mockMvcPerform(GET, "/orders/1/lines", 404, "[]");
  }

  @Test
  public void postOrderLineSuccessTest() throws Exception {
    when(orderOrderLineService.createOrderLineItem(anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 201, toJson(new OrderLineItem()));
  }

  @Test
  public void postOrderLineFailureTest() throws Exception {
    mockMvcPerform(POST, "/orders/1/lines", toJson(new OrderLineItem()), 500, "");
  }

  @Test
  public void postOrderLineEmptyBodyTest() throws Exception {
    mockMvcPerform(POST, "/orders/1/lines", 400, "");
  }

  @Test
  public void createOrderLine_InvalidJson_Code400() throws Exception {
    when(orderOrderLineService.createOrderLineItem(1, "{wrong}")).thenThrow(new IOException());
    mockMvcPerform(POST, "/orders/1/lines", "{wrong}", 400, "<h1>ERROR:</h1>\n"
        + " Invalid Json format");
  }

  @Test
  public void putOrderLineSuccessTest() throws Exception {
    when(orderOrderLineService.updateOrderLineItem(anyLong(), anyLong(), anyString())).thenReturn(new OrderLineItem());
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 200, toJson(new OrderLineItem()));
  }

  @Test
  public void putOrderLineNotFoundTest() throws Exception {
    mockMvcPerform(PUT, "/orders/1/lines/1", toJson(new OrderLineItem()), 404,"");
  }

  @Test
  public void putOrderLineEmptyBodyTest() throws Exception {
    mockMvcPerform(PUT, "/orders/1/lines/1", 400, "");
  }

  @Test
  public void updateOrderLine_InvalidJson_Code400() throws Exception {
    when(orderOrderLineService.updateOrderLineItem(1,1, "{wrong}")).thenThrow(new IOException());
    mockMvcPerform(PUT, "/orders/1/lines/1", "{wrong}", 400, "<h1>ERROR:</h1>\n"
        + " Invalid Json format");
  }

  @Test
  public void deleteOrderLineSuccessTest() throws Exception {
    when(orderOrderLineService.deleteOrderLineItem(anyLong(), anyLong())).thenReturn(new OrderLineItem());
    mockMvcPerform(DELETE, "/orders/1/lines/1", 200, toJson(new OrderLineItem()));
  }

  @Test
  public void deleteOrderLineNotFoundTest() throws Exception {
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
      String expectedResponseBody) throws Exception {
    mockMvcPerform(method, endpoint, "", expectedStatus, expectedResponseBody);
  }

  private void mockMvcPerform(String method, String endpoint, String requestBody, int expectedStatus,
      String expectedResponseBody) throws Exception {
    switch(method){

      case GET:
        mockMvc.perform(get(endpoint)).andExpect(status().is(expectedStatus))
          .andExpect(content().string(expectedResponseBody)).andDo(print());
        break;

      case POST:
        mockMvc.perform(
            post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().is(expectedStatus))
            .andExpect(content().string(expectedResponseBody));
        break;

      case PUT:
        mockMvc.perform(
            put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().is(expectedStatus))
            .andExpect(content().string(expectedResponseBody));
        break;

      case DELETE:
        mockMvc.perform(delete(endpoint)).andExpect(status().is(expectedStatus))
            .andExpect(content().string(expectedResponseBody));
        break;

      default:
        logger.error("Unknown method '{}' given to mockMvcPerform", method);
    }
  }

  private List<OrderDetail> getOrderDetails() {
    List<OrderDetail> orderDetails = new ArrayList<>();
    Address address = new Address(
        "111 N Canal St",
        "700",
        "Chicago",
        "IL",
        "60606",
        "United States"
    );

    Product product = new Product("test", 2.50);

    List<Shipment> shipments = new ArrayList<>();
    Shipment testShipment = new Shipment(
        1,
        1,
        1,
        getOrderLineSummaries(getOrderLineItems()),
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));
    shipments.add(testShipment);
    shipments.add(testShipment);

    Order order1 = getOrder1();
    order1.setOrderNumber(1L);
    Order order2 = getOrder2();
    order2.setOrderNumber(2L);

    orderDetails.add(new OrderDetail(
        order1.getOrderNumber(),
        address,
        order1.getTotalPrice(),
        getOrderLineSummaries(getOrderLineItems()),
        shipments
    ));
    orderDetails.add(new OrderDetail(
        order2.getOrderNumber(),
        address,
        order2.getTotalPrice(),
        getOrderLineSummaries(getOrderLineItems()),
        shipments
    ));

    return orderDetails;
  }

  private List<OrderLineSummary> getOrderLineSummaries(List<OrderLineItem> orderLineItems) {
    List<OrderLineSummary> orderLineSummaries = new ArrayList<>();

    orderLineItems.forEach(orderLineItem ->
        orderLineSummaries.add(new OrderLineSummary(
            "test",
            orderLineItem.getQuantity()
        )));

    return orderLineSummaries;
  }

  private List<Order> getOrders() {
    return Arrays.asList(getOrder1(), getOrder2());
  }

  private List<OrderLineItem> getOrderLineItems() {
    return Arrays.asList(getOrderLineItem1(), getOrderLineItem2(), getOrderLineItem3());
  }

  private Order getOrder1() {
    List<OrderLineItem> list = new ArrayList<>();
    list.add(getOrderLineItem1());
    list.add(getOrderLineItem3());
    return new Order(
        1,
        LocalDateTime.of(2018,9,12,2,0),
        1,
        list
    );
  }

  private Order getOrder2() {
    List<OrderLineItem> list = new ArrayList<>();
    list.add(getOrderLineItem1());
    list.add(getOrderLineItem2());
    return new Order(
        1,
        LocalDateTime.of(2018,9,9,5,30),
        2,
        list
    );
  }

  private OrderLineItem getOrderLineItem1() {
    return new OrderLineItem(1,3, 25.00, 1);
  }

  private OrderLineItem getOrderLineItem2() {
    return new OrderLineItem(2,5, 15.00, 2);
  }

  private OrderLineItem getOrderLineItem3() {
    return new OrderLineItem(3,8, 40.00, 3);
  }
}
