package com.gvormbrock.test.repository;

import com.gvormbrock.test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // @Query("select u from UserTable u where u.name = ?1 and u.birthday = ?2")
    List<User> findByNameAndBirthday(String name, Date birthday);
}
