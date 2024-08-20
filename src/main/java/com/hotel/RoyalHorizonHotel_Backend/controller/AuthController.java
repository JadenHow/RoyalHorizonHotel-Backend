package com.hotel.RoyalHorizonHotel_Backend.controller;

import com.hotel.RoyalHorizonHotel_Backend.model.Role;
import com.hotel.RoyalHorizonHotel_Backend.model.User;
import com.hotel.RoyalHorizonHotel_Backend.security.JwtUtils;
import com.hotel.RoyalHorizonHotel_Backend.service.IUserService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {

        List<String> missingFields = validateRegisterUser(request);

        if (!missingFields.isEmpty()) {
            String missingFieldsString = String.join(", ", missingFields);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Missing fields: " + missingFieldsString);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (request.getAdmin() != null && request.getAdmin()) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        try {
            User userCreated = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with email " + request.getEmail() + " already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userService.getUserByEmail(request.getEmail());
            String jwtToken = jwtUtils.generateToken(user);

            AuthenticationResponse response = new AuthenticationResponse();
            response.setJwt(jwtToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication credentials not found");
        }
    }

    private static List<String> validateRegisterUser(RegisterRequest request) {
        List<String> missingFields = new ArrayList<>();

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            missingFields.add("First Name");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            missingFields.add("Last Name");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            missingFields.add("Email");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            missingFields.add("Password");
        }
        return missingFields;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Boolean admin;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationRequest {
        private String email;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationResponse {
        private String jwt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
