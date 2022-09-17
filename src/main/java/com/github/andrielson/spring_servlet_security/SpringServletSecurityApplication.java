package com.github.andrielson.spring_servlet_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@SpringBootApplication
@RestController
public class SpringServletSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServletSecurityApplication.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                .failureHandler(((request, response, exception) -> response.sendError(HttpServletResponse.SC_BAD_REQUEST)))
                .permitAll());
        http.rememberMe(rememberMe -> rememberMe
                .alwaysRemember(true)
                .rememberMeServices(new SpringSessionRememberMeServices()));
        http.exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
        http.logout(logout -> logout
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                .permitAll());
        return http.build();
    }

    @RequestMapping(value = "login", method = RequestMethod.HEAD)
    public ResponseEntity<Void> csrfToken() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hello")
    public ResponseEntity<Principal> hello(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
