package com.gvormbrock.test;

import com.gvormbrock.test.dto.UserDto;
import com.gvormbrock.test.exception.ErrorServerException;
import com.gvormbrock.test.exception.NotFoundException;
import com.gvormbrock.test.model.Country;
import com.gvormbrock.test.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceTests {
    @Autowired
    UserService userService;

    private Date birthdayDate1;
    private Date birthdayDate2;

    private final Country countryFr = new Country();
    private final Country countryEn = new Country();


    @BeforeAll
    public void setup() {
        Instant instant1 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-20", Instant::from);
        birthdayDate1 = new Date(instant1.getEpochSecond() * 1000L);
        Instant instant2 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-21", Instant::from);
        birthdayDate2 = new Date(instant2.getEpochSecond() * 1000L);
        countryFr.setCountryCode("fr");
        countryFr.setName("France");
        countryEn.setCountryCode("en");
        countryEn.setName("England");
    }

    @Test
    void testSaveOk() {
        UserDto userDto = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr").build();
        userService.save(userDto, true);
        UserDto ret = userService.findByNameAndBirthday("ServiceTest", birthdayDate1);
        Assertions.assertEquals(birthdayDate1, ret.getBirthday());
        Assertions.assertEquals("ServiceTest", ret.getName());
    }

    @Test
    void testSaveThrowsExceptionCauseUserIsNotOfLegalAge() {
        LocalDate now = LocalDate.now();
        Date nowSQL = Date.valueOf(now);
        UserDto user = UserDto.builder()
            .name("ServiceTest")
            .birthday(nowSQL)
            .countryCode("fr").build();
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
    }

    @Test
    void testSaveThrowsExceptionCauseNoMandatoryEntityFields() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .build();
        // Country is mandatory
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
        user.setCountryCode("fr");
        // Name must not be blank
        user.setName("  ");
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
        // Birthday must not be null
        user.setName("ServiceTest1");
        user.setBirthday(null);
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
    }

    @Test
    void testSaveThrowsExceptionCauseUserIsNotFrench() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("en")
                .build();
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
    }

    @Test
    void testSaveThrowsExceptionCauseCountryDoesNotExists() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("xx")
                .build();
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
    }

    @Test
    void testSaveThrowsExceptionCauseUserExists() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr")
                .build();
        Assertions.assertDoesNotThrow(() -> userService.save(user, true));
        Assertions.assertThrows(ErrorServerException.class, () -> userService.save(user, true));
    }

    @Test
    void testUpdateUser() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr")
                .build();
        UserDto res = userService.save(user, false);
        user.setName("ServiceTest2");
        Assertions.assertDoesNotThrow(() -> userService.save(res, false));
    }

    @Test
    void testDelete() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr")
                .build();
        userService.save(user, true);
        UserDto ret = userService.findByNameAndBirthday("ServiceTest", birthdayDate1);
        Long id = ret.getId();
        userService.deleteById(id);
        ret = userService.findByNameAndBirthday("ServiceTest", birthdayDate1);
        Assertions.assertNull(ret);
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.deleteById(id));
    }

    @Test
    void testListUsers() {
        UserDto user1 = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr")
                .build();
        UserDto user2 = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate2)
                .countryCode("fr")
                .build();
        userService.save(user1, true);
        userService.save(user2, true);
        List<UserDto> res = userService.findAll();
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(2, res.size());
    }

    @Test
    void testFindUserById() {
        UserDto user = UserDto.builder()
                .name("ServiceTest")
                .birthday(birthdayDate1)
                .countryCode("fr")
                .build();
        UserDto savedDto = userService.save(user, true);
        Long id = savedDto.getId();
        UserDto res = userService.findById(id);
        Assertions.assertEquals(id, res.getId());
    }

    @Test
    void testFindUserByIdThrowsExceptionCauseNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findById(Long.MAX_VALUE));
    }

}
