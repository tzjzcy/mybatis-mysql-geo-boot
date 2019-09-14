package com.carson.service;

import com.carson.mapper.UserMapper;
import com.carson.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> selectAllUser() {
        return userMapper.selectAll();
    }

    public int addUser(User user) {
        String id= StringUtils.replace(UUID.randomUUID().toString(),"-","");
        user.setId(id);
        return userMapper.insert(user);
    }
}
