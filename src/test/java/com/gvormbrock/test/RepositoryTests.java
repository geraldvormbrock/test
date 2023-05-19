package com.gvormbrock.test;

import com.gvormbrock.test.model.Country;
import com.gvormbrock.test.model.User;
import com.gvormbrock.test.repository.CountryRepository;
import com.gvormbrock.test.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Date birthdayDate1;
    private Date birthdayDate2;

    private final Country countryFr = new Country();


    @BeforeAll
    public void setup() {
        Instant instant1 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-20", Instant::from);
        birthdayDate1 = new Date(instant1.getEpochSecond() * 1000L);
        Instant instant2 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-21", Instant::from);
        birthdayDate2 = new Date(instant2.getEpochSecond() * 1000L);
        countryFr.setCountryCode("fr");
        countryFr.setName("France");
        countryFr.setId(1L);

        Optional<Country> country = countryRepository.findByCountryCode("fr");
        Assertions.assertTrue(country.isPresent());

        User user = new User();
        user.setName("UserRepositoryTest");
        user.setBirthday(birthdayDate1);
        user.setCountryOfResidence(country.get());

        userRepository.save(user);
    }

    @Test
    void testCountryFindByName() {
        Optional<Country> result = countryRepository.findByName("France");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("France", result.get().getName());
        Assertions.assertEquals( "fr", result.get().getCountryCode());
    }

    @Test
    void testCountryFindByCountryCode() {
        Optional<Country> result = countryRepository.findByCountryCode("fr");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("France", result.get().getName());
        Assertions.assertEquals( "fr", result.get().getCountryCode());
    }

    @Test
    void testUserFindByNameAndBirthday() {
        User user = new User();
        user.setName("UserRepositoryTest");
        user.setBirthday(birthdayDate2);
        Optional<Country> country = countryRepository.findByCountryCode("fr");
        Assertions.assertTrue(country.isPresent());
        user.setCountryOfResidence(country.get());

        userRepository.save(user);
        List<User> result = userRepository.findByNameAndBirthday("UserRepositoryTest", birthdayDate2);
        Assertions.assertTrue(result != null && result.size() == 1);
        Assertions.assertEquals("UserRepositoryTest", result.get(0).getName());
        Assertions.assertEquals( birthdayDate2, result.get(0).getBirthday());
    }

    @Test
    void testUserDelete() {
        // Create user
        User user = new User();
        user.setName("ServiceTest");
        user.setBirthday(birthdayDate1);
        user.setCountryOfResidence(countryFr);
        userRepository.save(user);

        // Retrieve user id
        List<User> ret = userRepository.findByNameAndBirthday("ServiceTest", birthdayDate1);
        Long id = ret.get(0).getId();

        // Delete user and try to retrieve it
        userRepository.deleteById(id);
        ret = userRepository.findByNameAndBirthday("ServiceTest", birthdayDate1);
        Assertions.assertTrue(ret.isEmpty());
    }
}
