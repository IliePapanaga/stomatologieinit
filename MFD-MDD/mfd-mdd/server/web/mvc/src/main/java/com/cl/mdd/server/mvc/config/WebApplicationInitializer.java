package com.cl.mdd.server.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;

@Component
public class WebApplicationInitializer implements ServletContextInitializer {

    @Value("${security.cookies.sessionId.name:SESSIONID}")
    private String sessionIdName;

    @Value("${security.cookies.httpOnly:true}")
    private boolean httpOnlyCookies;

    @Value("${security.cookies.secure:true}")
    private boolean secureCookies;

    private ServletContext configureSessionCookie(ServletContext servletContext) {
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setName(sessionIdName);
        sessionCookieConfig.setSecure(secureCookies);
        sessionCookieConfig.setHttpOnly(httpOnlyCookies);
        return servletContext;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        configureSessionCookie(servletContext);
    }
}
