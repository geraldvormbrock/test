package com.gvormbrock.test.controller;

import com.gvormbrock.test.dto.UserDto;
import com.gvormbrock.test.exception.*;
import com.gvormbrock.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<UserDto> listUsers() {
        return userService.findAll();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}")
    public UserDto findUserById(@PathVariable long id) {
        return userService.findById(id);
    }

    /**
     * PUT a User:
     * The request parameter is isTestIfExists can be true (default) or false. If true, we can not update an existing user.
     * The new user must contain a country of France only.
     * The country can be given by its name like 'France' or the countryCode like 'fr'.
     * Moreover, the new user must be at least 18 years old to be created.
     * If a constraints is not respected, an exception is thrown and the returned server code is 500.
     * If everything is Ok the returned server code is 200
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/users")
    public UserDto createUser(@RequestBody UserDto user,
                              @RequestParam(name="verify", defaultValue = "true") boolean isTestIfExists)
            throws ErrorServerException {
        return userService.save(user, isTestIfExists);
    }

    /**
     * POST a User:
     * The user must not exist.
     * The new user must contain a country of France only.
     * The country can be given by its name like 'France' or the countryCode like 'fr'.
     * Moreover, the new user must be at least 18 years old to be created.
     * If a constraints is not respected, the returned server code is 500 with th description as ErrorDetails.
     * If everything is Ok the returned server code is 200
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto user) {
        return userService.save(user, true);
    }

    /**
     * The new user must contain a country of France only.
     * The country can be given by its name like 'France' or the countryCode like 'fr'.
     * Moreover, the new user must be at least 18 years old to be created.
     * If a constraints is not respected, an exception is thrown and the returned server code is 500.
     * If everything is Ok the returned server code is 200
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

}
