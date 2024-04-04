package com.example.demo.controller;

import com.example.demo.dto.request.AuthenticationRequestDto;
import com.example.demo.dto.request.RegisterRequestDto;
import com.example.demo.dto.response.AuthenticationResponseDto;
import com.example.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {
     private final AuthenticationService authService;
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate( @RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

}
