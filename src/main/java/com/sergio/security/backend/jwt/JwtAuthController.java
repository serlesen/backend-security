package com.sergio.security.backend.jwt;

import com.sergio.security.backend.session.SignInDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.CharBuffer;

@RestController
public class JwtAuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserAuthProvider userAuthProvider;

    @PostMapping("/auth/jwt/sign")
    public ResponseEntity<String> signIn(
            @RequestBody SignInDto signInDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(signInDto.username());

        if (passwordEncoder.matches(CharBuffer.wrap(signInDto.password()), userDetails.getPassword())) {
            return ResponseEntity.ok(userAuthProvider.createToken(signInDto));
        }
        return ResponseEntity.status(401).build();
    }

}
