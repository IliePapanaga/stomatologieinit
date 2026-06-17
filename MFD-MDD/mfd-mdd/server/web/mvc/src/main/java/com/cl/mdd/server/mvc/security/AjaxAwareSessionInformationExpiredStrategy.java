package com.cl.mdd.server.mvc.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Decorator which handles session expiration during AJAX request:
 * <p>
 * Returns 200 if authentication succeeded.
 * Returns 401 if authentication failed.
 * <p>
 * Fallbacks to provided handlers if authentication request is not AJAX based.
 */
public class AjaxAwareSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SessionInformationExpiredStrategy defaultStrategy;

    public AjaxAwareSessionInformationExpiredStrategy(SessionInformationExpiredStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    protected boolean requestIsAjax(HttpServletRequest request) {
        return StringUtils.defaultIfEmpty(request.getHeader("X-Requested-With"), "").equals("XMLHttpRequest");
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to proceed.");
            return;
        }
        if (requestIsAjax(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            defaultStrategy.onExpiredSessionDetected(event);
        }
    }
}
