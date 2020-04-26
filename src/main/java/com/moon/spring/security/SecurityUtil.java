package com.moon.spring.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author benshaoye
 */
public final class SecurityUtil {

    public static SecurityContext getContext() { return SecurityContextHolder.getContext(); }

    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
    }

    public static Authentication getAuthentication() { return getContext().getAuthentication(); }

    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth == null ? false : auth.isAuthenticated();
    }

    public static boolean isAnonymous() {
        Authentication auth = getAuthentication();
        return auth == null ? false : auth instanceof AnonymousAuthenticationToken;
    }
}
