package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.repository;

import edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("select u from AppUser u where u.password is null or u.password = ''")
    List<AppUser> findUsersWithMissingPassword();
}
