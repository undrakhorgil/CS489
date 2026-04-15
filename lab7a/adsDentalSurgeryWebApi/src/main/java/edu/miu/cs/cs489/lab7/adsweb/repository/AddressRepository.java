package edu.miu.cs.cs489.lab7.adsweb.repository;

import edu.miu.cs.cs489.lab7.adsweb.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("""
            select distinct a from Address a
            left join fetch a.patients
            order by a.city asc, a.addressId asc
            """)
    List<Address> findAllWithPatientsOrderByCityAsc();
}
