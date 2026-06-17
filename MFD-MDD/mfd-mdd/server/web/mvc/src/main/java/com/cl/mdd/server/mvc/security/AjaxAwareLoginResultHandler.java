package com.cl.mdd.server.mvc.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Decorator which handles AJAX authentication:
 *
 * Returns 200 if authentication succeeded.
 * Returns 403 if authentication failed caused by disabled user.
 * Returns 401 if authentication failed caused by any other reasons.
 *
 * Fallbacks to provided handlers if authentication request is not AJAX based.
 */
public class AjaxAwareLoginResultHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AuthenticationSuccessHandler defaultSuccessHandler;

    private AuthenticationFailureHandler defaultFailureHandler;

    public AjaxAwareLoginResultHandler(AuthenticationSuccessHandler success, AuthenticationFailureHandler failure) {
        this.defaultSuccessHandler = success;
        this.defaultFailureHandler = failure;
    }

    protected boolean requestIsAjax(HttpServletRequest request) {
        return StringUtils.defaultIfEmpty(request.getHeader("X-Requested-With"), "").equals("XMLHttpRequest");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to proceed.");
            return;
        }
        if(requestIsAjax(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to proceed.");
            return;
        }
        if(requestIsAjax(request)) {
            response.sendError(exception instanceof DisabledException ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
        else {
            defaultFailureHandler.onAuthenticationFailure(request, response, exception);
        }
    }
}
