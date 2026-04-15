package edu.miu.cs.cs489.lab7.adsweb.dto.address;

import edu.miu.cs.cs489.lab7.adsweb.model.Address;

public record AddressResponse(
        Long addressId,
        String street,
        String city,
        String state,
        String zipCode
) {
    public static AddressResponse fromEntity(Address a) {
        if (a == null) {
            return null;
        }
        return new AddressResponse(
                a.getAddressId(),
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getZipCode());
    }
}
