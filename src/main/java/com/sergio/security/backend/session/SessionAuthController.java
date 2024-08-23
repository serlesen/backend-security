package com.sergio.security.backend.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.CharBuffer;
import java.util.Collections;

@RestController
public class SessionAuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/auth/session/sign")
    public ResponseEntity<Void> signIn(
            @RequestBody SignInDto signInDto, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(signInDto.username());

        if (passwordEncoder.matches(CharBuffer.wrap(signInDto.password()), userDetails.getPassword())) {
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList()));
            securityContextRepository.saveContext(context, request, response);

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(401).build();
    }

}
