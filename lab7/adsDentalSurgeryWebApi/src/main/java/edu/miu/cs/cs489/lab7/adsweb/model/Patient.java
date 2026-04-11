package edu.miu.cs.cs489.lab7.adsweb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Address mailingAddress;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "patient_ref", unique = true, length = 16)
    private String patientRef;
}
