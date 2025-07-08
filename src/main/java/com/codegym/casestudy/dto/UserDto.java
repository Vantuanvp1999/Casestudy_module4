package com.codegym.casestudy.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String email;
    private String password;
    // Bạn có thể thêm các trường khác như firstName, lastName... nếu cần
}
