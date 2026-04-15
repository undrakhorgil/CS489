package edu.miu.cs.cs489.lab7b.welcome.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "contact_phone_number", nullable = false, length = 30)
    private String contactPhoneNumber;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "mailing_address_id", nullable = false)
    private Address mailingAddressEntity;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    public Patient() {
    }

    public String getMailingAddress() {
        return mailingAddressEntity == null ? null : mailingAddressEntity.toDisplayLine();
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddressEntity = mailingAddress == null ? null : Address.fromLine(mailingAddress);
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getMailingAddressEntity() {
        return mailingAddressEntity;
    }

    public void setMailingAddressEntity(Address mailingAddressEntity) {
        this.mailingAddressEntity = mailingAddressEntity;
    }
}

