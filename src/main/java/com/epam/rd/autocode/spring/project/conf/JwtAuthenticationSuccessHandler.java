package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String token = jwtService.generateToken(authentication);

        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day

        response.addCookie(cookie);

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
