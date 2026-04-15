package edu.miu.cs.cs489.lesson7.citylibraryapp.dto.publisher;

import edu.miu.cs.cs489.lesson7.citylibraryapp.dto.address.AddressRequest;

public record PublisherRequest(
        String name,
        AddressRequest primaryAddress
) {
}
