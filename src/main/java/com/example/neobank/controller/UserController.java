package com.example.neobank.controller;

import com.example.neobank.dto.UserDto;
import com.example.neobank.entity.User;
import com.example.neobank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.registerUser(userDto));

    }
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto){
       var authObject= userService.authenticateUser(userDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,(String) authObject.get("token"))
                .body(authObject.get("user"));

    }

}// 2:42:04