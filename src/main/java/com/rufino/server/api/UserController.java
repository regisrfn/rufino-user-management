package com.rufino.server.api;

import com.rufino.server.exception.ApiHandlerException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = { "/", "api/v1/user" })
public class UserController extends ApiHandlerException {

    @GetMapping("")
    public String home() {
        return "Welcome Home";
    }

}