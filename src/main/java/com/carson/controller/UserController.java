package com.carson.controller;

import com.carson.pojo.User;
import com.carson.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/userList")
    public List<User> userList() {
        return userService.selectAllUser();
    }

    @PostMapping("addUser")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }
}
