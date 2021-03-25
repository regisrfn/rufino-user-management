package com.rufino.server;

import static com.rufino.server.constant.ExceptionConst.ACCOUNT_LOCKED;
import static com.rufino.server.constant.ExceptionConst.EMAIL_NOT_AVAILABLE;
import static com.rufino.server.constant.ExceptionConst.INCORRECT_CREDENTIALS;
import static com.rufino.server.constant.SecurityConst.JWT_TOKEN_HEADER;
import static com.rufino.server.constant.SecurityConst.MAXIMUM_NUMBER_OF_ATTEMPTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;
import com.rufino.server.services.LoginAttemptService;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@AutoConfigureMockMvc
public class PostRequestTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoginAttemptService loginCache;
    @Autowired
    private Dotenv dotenv;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        loginCache.clearAll();
        jdbcTemplate.update("DELETE FROM users_authority_list");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void itShouldSaveUser() throws Exception {
        String EMAIL_TEST = dotenv.get("EMAIL_TEST");
        JSONObject my_obj = new JSONObject();

        MvcResult result = saveUserAndCheck(my_obj);

        User response = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo(EMAIL_TEST);
        my_obj = new JSONObject();
    }

    @Test
    void itShouldNotSaveUser_emailExists() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("username", "user1234");
        my_obj.put("firstName", "John");
        my_obj.put("lastName", "Doe");
        my_obj.put("username", "user1234");
        my_obj.put("email", "john@gmail.com");

        createDefaultUser();       

        mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(EMAIL_NOT_AVAILABLE)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void itShouldLoginUser() throws Exception {
        JSONObject my_obj = new JSONObject();

        createDefaultUser();

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

        createDefaultUser();

        my_obj = new JSONObject();
        my_obj.put("username", "user123");
        my_obj.put("password", "1234567");
        for (int i = 0; i < MAXIMUM_NUMBER_OF_ATTEMPTS; i++) {
            mockMvc.perform(
                    post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(INCORRECT_CREDENTIALS)))
                    .andExpect(status().isBadRequest()).andReturn();

        }
        mockMvc.perform(post("/api/v1/user/login").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(ACCOUNT_LOCKED)))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    private MvcResult saveUserAndCheck(JSONObject my_obj) throws JSONException, Exception {
        String EMAIL_TEST = dotenv.get("EMAIL_TEST");

        my_obj.put("username", "user123");
        my_obj.put("firstName", "John");
        my_obj.put("lastName", "Doe");
        my_obj.put("email", EMAIL_TEST);

        MvcResult result = mockMvc.perform(
                post("/api/v1/user/register").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();
        return result;
    }

    private User createDefaultUser(){
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setUsername("user123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("123456"));
        return userDao.saveOrUpdateUser(user);
    }

}
