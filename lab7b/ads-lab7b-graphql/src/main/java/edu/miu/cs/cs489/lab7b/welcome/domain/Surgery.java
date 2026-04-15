package edu.miu.cs.cs489.lab7b.welcome.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "surgeries")
public class Surgery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surgery_id")
    private Long surgeryId;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_address_id", nullable = false)
    private Address locationAddressEntity;

    @Column(name = "telephone_number", nullable = false, length = 30)
    private String telephoneNumber;

    public Surgery() {
    }

    public String getLocationAddress() {
        return locationAddressEntity == null ? null : locationAddressEntity.toDisplayLine();
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddressEntity = locationAddress == null ? null : Address.fromLine(locationAddress);
    }

    public Long getSurgeryId() {
        return surgeryId;
    }

    public void setSurgeryId(Long surgeryId) {
        this.surgeryId = surgeryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Address getLocationAddressEntity() {
        return locationAddressEntity;
    }

    public void setLocationAddressEntity(Address locationAddressEntity) {
        this.locationAddressEntity = locationAddressEntity;
    }
}

