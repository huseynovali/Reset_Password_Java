package com.example.demo.service;

import com.example.demo.dto.request.AuthenticationRequestDto;
import com.example.demo.dto.request.RegisterRequestDto;
import com.example.demo.dto.response.AuthenticationResponseDto;
import com.example.demo.module.Role;
import com.example.demo.module.User;
import com.example.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationMeneger;
    private final JavaSmtpGmailSenderService senderService;

    public String register(RegisterRequestDto request) {
        User user = userRepo.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeException("User already exists");
        }

        user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepo.save(user);

//        var jwtToken = jwtService.genereateToken(user);
//        return AuthenticationResponseDto.builder()
//                .token(jwtToken)
//                .build();
    return  "User registered successfully";

    }


    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
         authenticationMeneger.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", user.getAuthorities().stream().map(Object::toString).toArray());
        claims.put("userId", user.getId());
        String token = jwtService.generateToken( claims,user);
        senderService.sendEmail("huseynovali612@gmail.com","This is subject","This is email body");
        return AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }


    public void sendOtpEmail(String email) {
        int otp = (int) (Math.random() * 10000);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setOtp(otp);
        user.setOtpCount(3);
        userRepo.save(user);
       senderService.sendEmail(email,"Reset Password","Your Otp Code :" + otp);
    }
    public boolean verifyOtp(String email, int otp) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if( user.getOtpCount() == 0) {
            throw new RuntimeException("You have reached the limit of attempts");
        }else if (user.getOtp() == otp) {
            user.setOtp(0);
            user.setOtpCount(3);
            userRepo.save(user);
            return true;
        } else {
            user.setOtpCount(user.getOtpCount() - 1);
            userRepo.save(user);
            return false;
        }



    }
    public void resetPassword(String email, String password) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
    }

}
