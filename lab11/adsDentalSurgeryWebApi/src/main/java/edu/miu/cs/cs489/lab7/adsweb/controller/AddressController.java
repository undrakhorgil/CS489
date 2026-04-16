package edu.miu.cs.cs489.lab7.adsweb.controller;

import edu.miu.cs.cs489.lab7.adsweb.dto.address.AddressWithPatientsResponse;
import edu.miu.cs.cs489.lab7.adsweb.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adsweb/api/v1")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressWithPatientsResponse>> listAddresses() {
        return ResponseEntity.ok(addressService.getAllAddressesSortedByCityWithPatients());
    }
}
