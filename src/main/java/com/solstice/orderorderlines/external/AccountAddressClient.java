package com.solstice.orderorderlines.external;

import com.solstice.orderorderlines.model.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "account-address-service", fallback = AccountAddressFallback.class)
public interface AccountAddressClient {
  @RequestMapping("/accounts/{accountId}/address/{addressId}")
  Address getAddressByAccountIdAndAddressId(
      @PathVariable("accountId") long accountId,
      @PathVariable("addressId") long addressId);
}

@Component
class AccountAddressFallback implements AccountAddressClient {

  @Override
  public Address getAddressByAccountIdAndAddressId(long accountId, long addressId) {
    return new Address("","","","","","");
  }
}