package com.daf.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password, String email) {}

    public record LoginResponse(String token) {}

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String jwt = this.authService.login(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(new LoginResponse(jwt));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String jwt = this.authService.register(registerRequest.username(), registerRequest.password(), registerRequest.email());
        return ResponseEntity.ok(jwt);
    }
}
