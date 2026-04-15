package edu.miu.cs.cs489.lab7.adsweb.repository;

import edu.miu.cs.cs489.lab7.adsweb.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    @Query("""
            select distinct p from Patient p
            left join p.mailingAddress a
            where lower(p.firstName) like lower(concat('%', :q, '%'))
               or lower(p.lastName) like lower(concat('%', :q, '%'))
               or lower(p.email) like lower(concat('%', :q, '%'))
               or lower(coalesce(p.patientRef, '')) like lower(concat('%', :q, '%'))
               or lower(p.contactPhoneNumber) like lower(concat('%', :q, '%'))
               or lower(a.street) like lower(concat('%', :q, '%'))
               or lower(a.city) like lower(concat('%', :q, '%'))
               or lower(a.state) like lower(concat('%', :q, '%'))
               or lower(coalesce(a.zipCode, '')) like lower(concat('%', :q, '%'))
            """)
    List<Patient> search(@Param("q") String searchString);
}
