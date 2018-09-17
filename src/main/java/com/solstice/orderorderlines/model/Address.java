package com.solstice.orderorderlines.model;

public class Address {
  private long id;
  private String street;
  private String apartment;
  private String city;
  private String state;
  private String zip;
  private String country;

  public Address(){

  }

  public Address(String street, String apartment, String city, String state, String zip,
      String country) {
    this.street = street;
    this.apartment = apartment;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.country = country;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getApartment() {
    return apartment;
  }

  public void setApartment(String apartment) {
    this.apartment = apartment;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Override
  public String toString() {
    return "Address{" +
        "id=" + id +
        ", street='" + street + '\'' +
        ", apartment='" + apartment + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zip='" + zip + '\'' +
        ", country='" + country + '\'' +
        '}';
  }
}
