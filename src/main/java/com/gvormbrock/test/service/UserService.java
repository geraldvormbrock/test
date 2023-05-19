package com.gvormbrock.test.service;

import com.gvormbrock.test.dto.UserDto;
import com.gvormbrock.test.exception.*;
import com.gvormbrock.test.mapper.UserMapper;
import com.gvormbrock.test.model.Country;
import com.gvormbrock.test.model.User;
import com.gvormbrock.test.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().
                map(userMapper::mapToUserDto).toList();
    }

    /**
     * Save a user under the following constraints which throws exceptions.
     *
     * @param userDto        the UserDto
     * @param isTestIfExists if true, test if the user exists and throw an exception if the user exists
     * @throws ErrorServerException if the country does not exist or nme is blank or birthday does not exist or user is
     * not french or have less than 18 years or any other validation error defined in UserDto
     */
    public UserDto save(UserDto userDto, boolean isTestIfExists)
            throws ErrorServerException {


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        if (!violations.isEmpty()) {
            throw new ErrorServerException(101, "Validation error : " + violations.iterator().next().getMessage());
        }

        Optional<Country> foundCountry;
        if (userDto.getCountryCode() != null) {
            foundCountry = countryService.findByCountryCode(userDto.getCountryCode());
        } else {
            foundCountry = countryService.findByName(userDto.getCountryName());
        }
        if (foundCountry.isEmpty()) {
            throw new ErrorServerException(450, "The country code " + userDto.getCountryCode() + " does not exists or the country name " + userDto.getCountryName() + " does not exists.");
        }

        Country country = foundCountry.get();
        User user = userMapper.mapToUser(userDto, country);
        if (!country.getCountryCode().equals("fr")) {
            throw new ErrorServerException(110, "User must be french to be added");
        }
        if (countYears(user.getBirthday()) < 18L) {
            throw new ErrorServerException(111, "User must be at least 18 years old to be added");
        }
        if (isTestIfExists) {
            UserDto findUserDto = this.findByNameAndBirthday(user.getName(), user.getBirthday());
            if (findUserDto != null) {
                throw new ErrorServerException(120, "The user of name " + user.getName() + " borne the " + user.getBirthday() + " ever exists");
            }
        }

        User savedUser =  userRepository.save(user);
        userDto.setId(savedUser.getId());
        // One of the two following attributes could have not been set
        userDto.setCountryCode(country.getCountryCode());
        userDto.setCountryName(country.getName());
        return userDto;
    }

    public void deleteById(Long id) {

        User res;
        try {
            res = userRepository.getReferenceById(id); // Throws EntityNotFoundException if id does not exist
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(401, e.getMessage());
        }
        // If user id does not exist:
        // In debug mode it throws the exception EntityNotFoundException but not in run mode and res has got the id set to id
        // That's why we test name.
        if (res.getName() == null) {
            throw new NotFoundException(401, "User of id = " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }

    public UserDto findById(Long id) throws NotFoundException {
        Optional<User> res = userRepository.findById(id);
        if (res.isEmpty()) {
            throw new NotFoundException(400, "User with id = " + id + " does not exists");
        }
        return userMapper.mapToUserDto(res.get());
    }

    public UserDto findByNameAndBirthday(String name, Date birthday) {
        List<User> ret = userRepository.findByNameAndBirthday(name, birthday);
        return ret.isEmpty() ? null : userMapper.mapToUserDto(ret.get(0));
    }

    private long countYears(java.sql.Date date) {
        if (date==null) {
            return 0L;
        }
        java.util.Date now = new java.util.Date();
        long diffInMillis = Math.abs(now.getTime() - date.getTime());
        return Math.abs(TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) / 365);
    }

}
