package com.daf.backend.security;

import com.daf.backend.model.User;
import com.daf.backend.model.UserRole;
import com.daf.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JWTService jwtService;

    public String register(String username, String password, String email) {
        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        UserRole role = userRepository.count() == 0 ? UserRole.Admin : UserRole.Viewer;
        newUser.setRole(role);

        userRepository.save(newUser);

        return jwtService.generate(username, role.toString());
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return jwtService.generate(username, user.getRole().toString());
    }
}
