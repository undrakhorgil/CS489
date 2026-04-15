package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "Address{" + "addressId=" + addressId + ", street='" + street + '\'' + ", city='" + city + '\''
                + ", state='" + state + '\'' + ", zipCode='" + zipCode + '\'' + '}';
    }
}
