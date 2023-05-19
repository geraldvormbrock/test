package com.gvormbrock.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvormbrock.test.dto.UserDto;
import com.gvormbrock.test.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.sql.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    static Date birthdayDate1;
    static Date birthdayDate2;

    static {
        Instant instant1 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-20", Instant::from);
        birthdayDate1 = new Date(instant1.getEpochSecond() * 1000L);
        Instant instant2 = DateFormatting.DATE_TIME_FORMATTER.parse("1971-10-21", Instant::from);
        birthdayDate2 = new Date(instant2.getEpochSecond() * 1000L);
    }

    @Test
    void testFindAllUsers() throws Exception {

        //BDDMockito.given(userService.findAll()).willReturn(Arrays.asList(userVormbrock, userDupont));

        this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"UserControllerTest\",\"birthday\":\"2000-10-19\",\"countryCode\":\"fr\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"UserControllerTest\",\"birthday\":\"2000-10-20\",\"countryName\":\"France\",\"countryCode\":null,\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        this.mvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].name").value("UserControllerTest"))
                .andExpect((jsonPath("$[1].name").value("UserControllerTest")));
    }

    @Test
    void testFindById() throws Exception {

        //BDDMockito.given(userService.findAll()).willReturn(Arrays.asList(userVormbrock, userDupont));

        ResultActions res = this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryCode\":\"fr\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        UserDto user = jsonGetUserDto(res);

        this.mvc.perform(MockMvcRequestBuilders.get("/users/"+ user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((jsonPath("$.name").value("TestUser")))
                .andExpect((jsonPath("$.birthday").value("2000-10-19")));
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/users/"+ Long.MAX_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testPostUser() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"VormbrockTest\",\"birthday\":\"2000-10-19\",\"countryCode\":\"fr\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testPutUserUpdate() throws Exception {

        //BDDMockito.given(userService.findAll()).willReturn(Arrays.asList(userVormbrock, userDupont));

        ResultActions res = this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"));

        UserDto user = jsonGetUserDto(res);

        this.mvc.perform(MockMvcRequestBuilders.put("/users?verify=false").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":"+ user.getId() +",\"gender\":\"Female\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    void testPutPostUserUpdateExistsError() throws Exception {

        //BDDMockito.given(userService.findAll()).willReturn(Arrays.asList(userVormbrock, userDupont));

        ResultActions res = this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"));

        UserDto user = jsonGetUserDto(res);

        this.mvc.perform(MockMvcRequestBuilders.put("/users?verify=true").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":"+ user.getId() +",\"gender\":\"Female\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        this.mvc.perform(MockMvcRequestBuilders.put("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":"+ user.getId() +",\"gender\":\"Female\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        this.mvc.perform(MockMvcRequestBuilders.put("/users?verify=false").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":"+ user.getId() +",\"gender\":\"Female\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect((jsonPath("$.gender").value("Female")));
    }

    @Test
    void testDeleteUser() throws Exception {

        //BDDMockito.given(userService.findAll()).willReturn(Arrays.asList(userVormbrock, userDupont));

        ResultActions res = this.mvc.perform(MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":null,\"gender\":\"Male\",\"name\":\"TestUser\",\"birthday\":\"2000-10-19\",\"countryName\":\"France\",\"phoneNumber\":null}"));

        UserDto user = jsonGetUserDto(res);

        this.mvc.perform(MockMvcRequestBuilders.delete("/users/"+ user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    void testDeleteUserNotFound() throws Exception {

        this.mvc.perform(MockMvcRequestBuilders.delete("/users/"+ Long.MAX_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private static UserDto jsonGetUserDto(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        String response = resultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, UserDto.class);
    }
}
