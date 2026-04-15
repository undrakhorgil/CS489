package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dentists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dentist_id")
    private Long dentistId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "contact_phone_number", nullable = false, length = 30)
    private String contactPhoneNumber;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 120)
    private String specialization;

    @Override
    public String toString() {
        return "Dentist{" + "dentistId=" + dentistId + ", firstName='" + firstName + '\'' + ", lastName='"
                + lastName + '\'' + ", email='" + email + '\'' + ", specialization='" + specialization + '\'' + '}';
    }
}
