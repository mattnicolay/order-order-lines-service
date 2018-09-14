package com.solstice.orderorderlines.external;

import com.solstice.orderorderlines.model.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("account-address-service")
public interface AccountAddressClient {
  @RequestMapping("/accounts/{accountId}/address/{addressId}")
  Address getAddressByAccountIdAndAddressId(
      @PathVariable("accountId") long accountId,
      @PathVariable("addressId") long addressId);
}
