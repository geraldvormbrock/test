package com.gvormbrock.test.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    // Unique id for each user in the db
    private Long id;

    @Pattern(regexp = "^Male$|^Female$", message = "Gender can be only Male or Female")
    @Size(min = 4, max = 6, message = "Gender must be minimum 4 characters and maximum 5 characters long")
    private String gender;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 50, message = "Name must be minimum 3 characters and maximum 50 characters long")
    private String name;

    @NotNull
    Date birthday;

    @Pattern(regexp = "^[a-zA-Z]+$", message = "Country name can be only be alphanumerical characters")
    @Size(min = 3, max = 50, message = "Country name must be minimum 3 characters and maximum 50 characters long")
    String countryName;

    @Pattern(regexp = "^[a-z]+$", message = "Country code can be only be lower case alphanumerical characters")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters long")
    String countryCode;

    @Size(min = 2, max = 50, message = "Phone number must be minimum 2 characters and maximum 50 characters long")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Can be + followed by a number or simply a number")
    String phoneNumber;
}