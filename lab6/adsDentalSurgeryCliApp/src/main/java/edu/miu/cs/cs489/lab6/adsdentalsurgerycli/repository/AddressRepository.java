package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
