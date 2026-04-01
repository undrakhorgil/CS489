package edu.miu.cs.cs489appsd.ads.domain;

public class Surgery {
    private Long surgeryId;
    private String name;
    private String locationAddress;
    private String telephoneNumber;

    public Surgery() {
    }

    public Surgery(Long surgeryId, String name, String locationAddress, String telephoneNumber) {
        this.surgeryId = surgeryId;
        this.name = name;
        this.locationAddress = locationAddress;
        this.telephoneNumber = telephoneNumber;
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

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
