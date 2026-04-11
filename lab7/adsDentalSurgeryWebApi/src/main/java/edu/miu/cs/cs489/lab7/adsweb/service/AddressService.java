package edu.miu.cs.cs489.lab7.adsweb.service;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressWithPatientsResponse;

import java.util.List;

public interface AddressService {

    List<AddressWithPatientsResponse> getAllAddressesSortedByCityWithPatients();
}
