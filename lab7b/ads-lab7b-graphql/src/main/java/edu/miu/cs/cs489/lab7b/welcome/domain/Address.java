package edu.miu.cs.cs489.lab7b.welcome.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(name = "zip_code", length = 16)
    private String zipCode;

    public Address() {
    }

    public Address(Long addressId, String street, String city, String state, String zipCode) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public static Address fromLine(String line) {
        if (line == null || line.isBlank()) {
            return new Address(null, "Unknown", "", "", null);
        }
        String[] parts = line.split(",");
        if (parts.length >= 3) {
            return new Address(null, parts[0].trim(), parts[1].trim(), parts[2].trim(), null);
        }
        return new Address(null, line.trim(), "", "", null);
    }

    public String toDisplayLine() {
        if (state == null || state.isEmpty()) return street;
        if (city == null || city.isEmpty()) return street;
        return street + ", " + city + ", " + state;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}

