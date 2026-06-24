package com.epam.rd.autocode.spring.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    public void testGenerateAndExtractToken() {
        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtService.generateToken(authentication);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    public void testIsTokenValid_Success() {
        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtService.generateToken(authentication);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testIsTokenValid_InvalidUsername() {
        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        UserDetails wrongUserDetails = new User(
                "wrong@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtService.generateToken(authentication);
        assertFalse(jwtService.isTokenValid(token, wrongUserDetails));
    }

    @Test
    public void testIsTokenExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);

        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtService.generateToken(authentication);
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testExtractClaim_InvalidToken() {
        String invalidToken = "some.invalid.token";
        assertNull(jwtService.extractUsername(invalidToken));
    }

    @Test
    public void testIsTokenValid_InvalidToken() {
        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        assertFalse(jwtService.isTokenValid("some.invalid.token", userDetails));
    }
}
