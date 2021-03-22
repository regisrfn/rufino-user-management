package com.rufino.server;

import static com.rufino.server.constant.ExceptionConst.EMAIL_NOT_AVAILABLE;
import static com.rufino.server.constant.ExceptionConst.INCORRECT_CREDENTIALS;
import static com.rufino.server.constant.ExceptionConst.ACCOUNT_LOCKED;

import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.model.User;

import org.hamcrest.core.Is;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class PostRequestTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM users_authority_list");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void itShouldSaveUser() throws Exception {
        JSONObject my_obj = new JSONObject();

        MvcResult result = saveUserAndCheck(my_obj);

        User response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        my_obj = new JSONObject();
    }

    @Test
    void itShouldNotSaveUser_emailExists() throws Exception {
        JSONObject my_obj = new JSONObject();

        saveUserAndCheck(my_obj);

        my_obj = new JSONObject();
        my_obj.put("username", "user1234");
        my_obj.put("firstName", "John");
        my_obj.put("lastName", "Doe");
        my_obj.put("email", "john@gmail.com");
        my_obj.put("password", "123456");

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(EMAIL_NOT_AVAILABLE.toUpperCase())))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void itShouldLoginUser() throws Exception {
        JSONObject my_obj = new JSONObject();

        saveUserAndCheck(my_obj);

        my_obj = new JSONObject();
        my_obj.put("username", "user123");
        my_obj.put("password", "123456");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        assertThat(mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER)).isNotBlank();

    }

    @Test
    void itShouldLockeUser_maxLoginAttempts() throws Exception {
        JSONObject my_obj = new JSONObject();

        saveUserAndCheck(my_obj);

        my_obj = new JSONObject();
        my_obj.put("username", "user123");
        my_obj.put("password", "123456");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        assertThat(mvcResult.getResponse().getHeader(JWT_TOKEN_HEADER)).isNotBlank();

        my_obj = new JSONObject();
        my_obj.put("username", "user123");
        my_obj.put("password", "1234567");
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(
                    post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(INCORRECT_CREDENTIALS.toUpperCase())))
                    .andExpect(status().isBadRequest()).andReturn();

        }
        my_obj.put("password", "123456");
        mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(ACCOUNT_LOCKED.toUpperCase())))
                .andExpect(status().isBadRequest()).andReturn();
    }

    private MvcResult saveUserAndCheck(JSONObject my_obj) throws JSONException, Exception {
        my_obj.put("username", "user123");
        my_obj.put("firstName", "John");
        my_obj.put("lastName", "Doe");
        my_obj.put("email", "john@gmail.com");
        my_obj.put("password", "123456");

        MvcResult result = mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();
        return result;
    }

}
