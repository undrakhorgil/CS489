package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surgeries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Surgery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surgery_id")
    private Long surgeryId;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_address_id", nullable = false)
    private Address locationAddress;

    @Column(name = "telephone_number", nullable = false, length = 30)
    private String telephoneNumber;

    @Override
    public String toString() {
        return "Surgery{" + "surgeryId=" + surgeryId + ", name='" + name + '\'' + ", locationAddress="
                + locationAddress + ", telephoneNumber='" + telephoneNumber + '\'' + '}';
    }
}
