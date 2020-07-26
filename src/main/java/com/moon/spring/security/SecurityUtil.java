package com.moon.spring.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * @author moonsky
 */
public final class SecurityUtil {

    public static SecurityContext getContext() { return SecurityContextHolder.getContext(); }

    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
    }

    public static Authentication getAuthentication() { return getContext().getAuthentication(); }

    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    public static boolean isAnonymous() {
        return getAuthentication() instanceof AnonymousAuthenticationToken;
    }

    public static String getLoggedUsername() {
        Authentication auth = getAuthentication();
        return auth == null || auth instanceof AnonymousAuthenticationToken ? null : auth.getName();
    }

    public static boolean isLoggedOf(String username) {
        return Objects.equals(getLoggedUsername(), username);
    }
}
