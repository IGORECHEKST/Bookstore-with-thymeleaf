package com.epam.rd.autocode.spring.project.conf;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        response.sendRedirect(request.getContextPath() + "/login?logout");
    }
}
