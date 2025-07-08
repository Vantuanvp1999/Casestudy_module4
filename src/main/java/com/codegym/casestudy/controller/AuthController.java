package com.codegym.casestudy.controller;

import com.codegym.casestudy.dto.UserDto;
import com.codegym.casestudy.entity.User;
import com.codegym.casestudy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Trang chủ (index)
    @GetMapping("/")
    public String homePage() {
        return "index"; // /templates/index.html
    }

    // Hiển thị form đăng nhập (do Spring Security xử lý POST /login)
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login"; // /templates/auth/login.html
    }

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register"; // /templates/auth/register.html
    }

    // Xử lý POST đăng ký
    @PostMapping("/register/save")
    public String register(@ModelAttribute("user") UserDto userDto, Model model) {
        User existingUser = userService.findByUsername(userDto.getUsername());
        if (existingUser != null) {
            model.addAttribute("user", userDto);
            model.addAttribute("error", "Tên đăng nhập đã tồn tại.");
            return "auth/register";
        }
        userService.saveUser(userDto);
        return "redirect:/login?register_success";
    }
}
