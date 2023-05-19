package com.gvormbrock.test.mapper;

import com.gvormbrock.test.dto.UserDto;
import com.gvormbrock.test.model.Country;
import com.gvormbrock.test.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapToUser(UserDto userDto, Country country) {
        return User.builder()
                .id(userDto.getId())
                .gender(userDto.getGender())
                .name(userDto.getName())
                .birthday(userDto.getBirthday())
                .countryOfResidence(country)
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    public UserDto mapToUserDto(User user) {
        @NotNull
        Country country = user.getCountryOfResidence();
        return UserDto.builder()
                .id(user.getId())
                .gender(user.getGender())
                .name(user.getName())
                .birthday(user.getBirthday())
                .countryName(country.getName())
                .countryCode(country.getCountryCode())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
