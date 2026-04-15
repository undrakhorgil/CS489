package edu.miu.cs.cs489.lesson6.citylibraryapp.repository;

import edu.miu.cs.cs489.lesson6.citylibraryapp.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

    @Query(value = "select p from Publisher p")
    List<Publisher> getMyCustomListOfPublisher();

    @Query(value = "select p from Publisher p where p.name = :name")
    Optional<Publisher> getMyCustomPublisherByName(String name);

    @Query(value = "SELECT * FROM `cs489-apsd-citylibrary-db2`.publishers p where p.name like 'Ap%'", nativeQuery = true)
    Optional<Publisher> getMyNativeCustomPublisherByName(String name);

    List<Publisher> findPublisherByNameIsStartingWith(String strNameStart);
}
