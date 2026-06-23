package com.epam.rd.autocode.spring.project.conf;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

@Component
public class LocaleFilter implements Filter {

    private final LocaleResolver localeResolver;

    public LocaleFilter(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String lang = request.getParameter("lang");
            if (lang != null && !lang.trim().isEmpty()) {
                Locale locale;
                if ("uk".equalsIgnoreCase(lang)) {
                    locale = new Locale("uk");
                } else {
                    locale = Locale.forLanguageTag(lang);
                }
                localeResolver.setLocale(httpRequest, httpResponse, locale);
                LocaleContextHolder.setLocale(locale);
            }
        }
        chain.doFilter(request, response);
    }
}
