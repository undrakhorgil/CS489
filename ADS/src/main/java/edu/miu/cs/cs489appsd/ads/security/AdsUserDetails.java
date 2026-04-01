package edu.miu.cs.cs489appsd.ads.security;

import edu.miu.cs.cs489appsd.ads.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AdsUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Role role;
    private final Long dentistId;
    private final Long patientId;

    public AdsUserDetails(String username, String password, Role role, Long dentistId, Long patientId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.dentistId = dentistId;
        this.patientId = patientId;
    }

    public Role getRole() {
        return role;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public Long getPatientId() {
        return patientId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String springRole = switch (role) {
            case OFFICE_MANAGER -> "ROLE_OFFICE_MANAGER";
            case DENTIST -> "ROLE_DENTIST";
            case PATIENT -> "ROLE_PATIENT";
        };
        return List.of(new SimpleGrantedAuthority(springRole));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
