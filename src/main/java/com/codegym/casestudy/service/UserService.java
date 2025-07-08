package com.codegym.casestudy.service;

import com.codegym.casestudy.dto.UserDto;
import com.codegym.casestudy.entity.User;

public interface UserService {
    void saveUser(UserDto userDto);
    User findByUsername(String username);
}