package edu.miu.cs.cs489appsd.ads.domain;

public class Account {
    private Long accountId;
    private String username;
    private String passwordHash;
    private Role role;
    /** Set when role is DENTIST */
    private Long dentistId;
    /** Set when role is PATIENT */
    private Long patientId;

    public Account() {
    }

    public Account(Long accountId, String username, String passwordHash, Role role,
                   Long dentistId, Long patientId) {
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.dentistId = dentistId;
        this.patientId = patientId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}
