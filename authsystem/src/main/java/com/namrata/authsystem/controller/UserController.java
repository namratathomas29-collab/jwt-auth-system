package com.namrata.authsystem.controller;

import com.namrata.authsystem.repository.UserRepository;
import com.namrata.authsystem.util.JwtUtil;
import com.namrata.authsystem.model.User;
import com.namrata.authsystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // ✅ allow browser requests
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // ✅ HEALTH CHECK
    @GetMapping("/")
    public String home() {
        return "Backend is running successfully 🚀";
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.registerUser(user);
        return "User registered successfully ✅";
    }

    // ✅ LOGIN → RETURNS ONLY TOKEN
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User loggedInUser = userService.loginUser(user.getEmail(), user.getPassword());

        if (loggedInUser != null) {
            return JwtUtil.generateToken(
                    loggedInUser.getEmail(),
                    loggedInUser.getRole()
            );
        } else {
            return "Invalid credentials";
        }
    }

    // ✅ PROFILE (ANY LOGGED-IN USER)
    @GetMapping("/profile")
    public User getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

    // ✅ ADMIN ONLY: GET ALL USERS
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ ADMIN ONLY: MAKE ADMIN
    @PutMapping("/make-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String makeAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
        return "User promoted to ADMIN ✅";
    }

    // ✅ TEST (AUTH CHECK)
    @GetMapping("/test")
    public String test() {
        return "You are authenticated 🎉";
    }

    // ✅ ADMIN TEST
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Admin access only 👑";
    }

    @GetMapping("/api/profile")
    public String profile() {
        return "Protected Profile Access Success ✅";
    }

    @GetMapping("/api/admin")
    public String adminAccess() {
        return "Admin Access Granted 👑";
    }
}