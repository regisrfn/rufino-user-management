package com.rufino.server.api;

import javax.validation.Valid;

import com.rufino.server.exception.ApiHandlerException;
import com.rufino.server.model.User;
import com.rufino.server.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = { "/", "api/v1/user" })
public class UserController extends ApiHandlerException {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public User register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

}
