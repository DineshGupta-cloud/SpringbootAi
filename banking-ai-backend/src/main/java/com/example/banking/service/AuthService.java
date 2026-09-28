package com.example.banking.service;

import com.example.banking.dto.*;
import com.example.banking.entity.Customer;
import com.example.banking.exception.ApiException;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.security.JwtUtil;
import com.example.banking.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SecurityService securityService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        Customer customer = Customer.builder()
                .name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone()).build();
        customer = customerRepository.save(customer);
        String token = jwtUtil.generateToken(customer.getId(), customer.getEmail());
        return AuthResponse.builder().token(token)
                .user(AuthResponse.UserDto.builder().id(customer.getId())
                        .name(customer.getName()).email(customer.getEmail()).build()).build();
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        String token = jwtUtil.generateToken(customer.getId(), customer.getEmail());
        return AuthResponse.builder().token(token)
                .user(AuthResponse.UserDto.builder().id(customer.getId())
                        .name(customer.getName()).email(customer.getEmail()).build()).build();
    }

    public AuthResponse.UserDto me() {
        var user = securityService.getCurrentUser();
        return AuthResponse.UserDto.builder().id(user.getCustomerId())
                .name(user.getName()).email(user.getEmail()).build();
    }
}
