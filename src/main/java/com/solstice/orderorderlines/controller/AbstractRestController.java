package com.solstice.orderorderlines.controller;

import com.solstice.orderorderlines.exception.HTTP400Exception;
import com.solstice.orderorderlines.exception.HTTP404Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AbstractRestController {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = {HTTP404Exception.class})
  public @ResponseBody String Handle404Exception(HTTP404Exception ex) {
    logger.error("HTTP404Exception encountered and handled: {}", ex.toString());
    return ex.getMessage();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {HTTP400Exception.class})
  public @ResponseBody String
  Handle400Exception(HTTP400Exception ex) {
    logger.error("HTTP400Exception encountered and handled: {}", ex.toString());
    return ex.getMessage();
  }

  public static <T> T checkResourceFound(final T resource) {
    if (resource == null) {
      throw new HTTP404Exception("Resource not found");
    }
    return resource;
  }
}
