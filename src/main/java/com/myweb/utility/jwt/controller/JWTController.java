package com.myweb.utility.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt/secured/hello")
public class JWTController {

    @GetMapping
    public String hello() {
        return "Hello World";
    }
}
