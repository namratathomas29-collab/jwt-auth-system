package com.namrata.authsystem.service;

import com.namrata.authsystem.model.User;
import com.namrata.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    // ✅ REGISTER USER
    public void registerUser(User user) {

        System.out.println("===== REGISTER DEBUG =====");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password RECEIVED: " + user.getPassword());

        // 🔴 CHECK: missing password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password is missing in request ❌");
        }

        // 🔴 CHECK: duplicate email (VERY IMPORTANT)
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already registered ❌");
        }

        // 🔐 Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        // 👑 ROLE LOGIC (first user = ADMIN)
        long userCount = userRepository.count();
        System.out.println("Total users in DB: " + userCount);

        if (userCount == 0) {
            user.setRole("ROLE_ADMIN");
            System.out.println("Assigned ROLE_ADMIN 👑");
        } else {
            user.setRole("ROLE_USER");
            System.out.println("Assigned ROLE_USER 👤");
        }

        userRepository.save(user);

        System.out.println("User saved successfully ✅");
        System.out.println("==========================");
    }

    // ✅ GET USER BY EMAIL
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ LOGIN USER
    public User loginUser(String email, String password) {

        System.out.println("========== LOGIN DEBUG ==========");
        System.out.println("Entered Email: " + email);
        System.out.println("Entered Password: " + password);

        // 🔴 Find user
        User user = userRepository.findByEmail(email);

        if (user == null) {
            System.out.println("User NOT found ❌");
            System.out.println("================================");
            return null;
        }

        System.out.println("User found in DB ✅");
        System.out.println("Stored Encrypted Password: " + user.getPassword());

        // 🔐 Check password
        boolean match = encoder.matches(password, user.getPassword());
        System.out.println("Password Match: " + match);

        if (!match) {
            System.out.println("Password incorrect ❌");
            System.out.println("================================");
            return null;
        }

        System.out.println("LOGIN SUCCESS ✅");
        System.out.println("================================");

        return user;
    }
}