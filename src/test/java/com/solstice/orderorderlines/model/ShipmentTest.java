package com.solstice.orderorderlines.model;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ShipmentTest {
  @Test
  public void jsonSerialize_ShipmentObjectWithoutOrderLineSummaries_SerializesProperly()
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Shipment shipment = objectMapper.readValue(
        "{\n"
            + "\"id\": \"1\","
          + " \"accountId\": \"1\",\n"
          + " \"shippingAddressId\": \"1\",\n"
          + " \"shippedDate\": [2018,9,8,12,30],\n"
          + " \"deliveryDate\": [2018,9,8,12,30]\n"
          + "}",
        Shipment.class
    );

    assertThat(shipment, is(notNullValue()));
  }
}
