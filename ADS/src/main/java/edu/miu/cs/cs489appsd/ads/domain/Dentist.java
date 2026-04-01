package edu.miu.cs.cs489appsd.ads.domain;

public class Dentist {
    private Long dentistId;
    private String firstName;
    private String lastName;
    private String contactPhoneNumber;
    private String email;
    private String specialization;

    public Dentist() {
    }

    public Dentist(Long dentistId, String firstName, String lastName, String contactPhoneNumber,
                   String email, String specialization) {
        this.dentistId = dentistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.email = email;
        this.specialization = specialization;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
