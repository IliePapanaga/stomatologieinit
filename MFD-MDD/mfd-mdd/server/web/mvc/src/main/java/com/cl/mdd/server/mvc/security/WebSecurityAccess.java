package com.cl.mdd.server.mvc.security;

import com.cl.mdd.server.core.security.SecurityAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class WebSecurityAccess extends SecurityAccess {

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private SessionRegistry sessionRegistry;

    void superLogin(String username) {
        super.login(username);
    }

    @Override
    public void login(String username) {
        superLogin(username);
        ServletRequestAttributes context = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        rememberMeServices.loginSuccess(context.getRequest(), context.getResponse(), currentSecurityContext().getAuthentication());
    }

    @Override
    public void logout(String username) {
        super.logout(username);
        Optional<Object> user = sessionRegistry.getAllPrincipals().stream().filter(o -> ((User) o).getUsername().equals(username)).findFirst();
        user.ifPresent(o -> sessionRegistry.getAllSessions(user.get(), false).forEach(SessionInformation::expireNow));
    }
}
