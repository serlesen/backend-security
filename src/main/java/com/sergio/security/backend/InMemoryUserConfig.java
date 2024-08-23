package com.sergio.security.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public InMemoryUserDetailsManager users() {
        UserDetails user = User.withUsername("current_user")
                .password(passwordEncoder.encode("current_pass"))
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
