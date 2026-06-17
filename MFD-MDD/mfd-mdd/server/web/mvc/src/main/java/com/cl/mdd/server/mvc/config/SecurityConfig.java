package com.cl.mdd.server.mvc.config;

import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.mvc.security.AjaxAwareLoginResultHandler;
import com.cl.mdd.server.mvc.security.AjaxAwareSessionInformationExpiredStrategy;
import com.cl.mdd.server.mvc.security.impersonation.ImpersonateUserDetailsChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan("com.cl.mdd.server.mvc.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";

    private static final String LOGIN_PAGE_URL = "/";

    private static final String IMPERSONATE_URL = "/impersonate";

    private static final String IMPERSONATE_FAILED_URL = IMPERSONATE_URL + "/failed";

    private static final String IMPERSONATE_SUCCESS_URL = IMPERSONATE_URL + "/success";

    private static final String IMPERSONATE_EXIT_URL = IMPERSONATE_URL + "/exit";

    private final UserDetailsService userDetailsService;

    private DataSource dataSource;

    @Value("${captcha.recaptcha.client:g-recaptcha-client}")
    private String reCaptchaClientHeader;

    @Value("${security.password.encryption.strength:12}")
    private int passwordStrength;

    @Value("${security.remember.token.validity.seconds:#{60*60*24*14}}")
    private int rememberTokenValiditySeconds;

    @Value("${security.cookies.secure:true}")
    private boolean secureCookies;

    @Value("${security.csrf.protection.enabled:true}")
    private boolean csrfProtectionEnabled;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, DataSource dataSource) {
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
    }

    @Bean
    public AjaxAwareLoginResultHandler loginResultHandler() {
        return new AjaxAwareLoginResultHandler(
                new SavedRequestAwareAuthenticationSuccessHandler(),
                new SimpleUrlAuthenticationFailureHandler("/login?error"));
    }

    @Bean
    public SessionInformationExpiredStrategy sessionExpirationStrategy() {
        return new AjaxAwareSessionInformationExpiredStrategy(new SimpleRedirectSessionInformationExpiredStrategy(LOGIN_PAGE_URL));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordStrength);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    @DependsOn("springSecurityFilterChain") // to be called after configure(HttpSecurity)
    public RememberMeServices rememberMeServices() throws Exception {
        return getHttp().getSharedObject(RememberMeServices.class);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        headers(http);
        form(http);
        rememberMe(http);
        authorization(http);
        filters(http);
        session(http);
    }

    private void session(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .maximumSessions(1)
                .expiredSessionStrategy(sessionExpirationStrategy())
                .sessionRegistry(sessionRegistry());
    }

    private void filters(HttpSecurity http) {
        http.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);
    }

    private SwitchUserFilter switchUserFilter() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userDetailsService);
        filter.setUserDetailsChecker(new ImpersonateUserDetailsChecker());
        filter.setSwitchUserUrl(IMPERSONATE_URL);
        filter.setSuccessHandler(new ForwardAuthenticationSuccessHandler(IMPERSONATE_SUCCESS_URL));
        filter.setFailureHandler(new ForwardAuthenticationFailureHandler(IMPERSONATE_FAILED_URL));
        filter.setExitUserUrl(IMPERSONATE_EXIT_URL);
        filter.setSwitchAuthorityRole(User.ROLE_PREVIOUS_SYSTEM_USER);
        return filter;
    }

    private void authorization(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/impersonate/exit").access("hasRole('" + User.ROLE_PREVIOUS_SYSTEM_USER + "')")
                .antMatchers("/impersonate").access("hasRole('" + User.ROLE_SYSTEM_USER + "')")
                .antMatchers("/api/v1/report/").access("hasRole('" + User.ROLE_SYSTEM_USER + "')")
                .antMatchers("/api/v1/graphql/public/**").permitAll()
                .antMatchers("/api/**").authenticated();
    }

    private void rememberMe(HttpSecurity http) throws Exception {
        http.rememberMe()
                .alwaysRemember(true)
                .tokenValiditySeconds(rememberTokenValiditySeconds)
                .tokenRepository(tokenRepository())
                .useSecureCookie(secureCookies);
    }

    private JdbcTokenRepositoryImpl tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    private void form(HttpSecurity http) throws Exception {
        http.httpBasic().and().formLogin();  // order matters to reply with 401 to unauthenticated AJAX requests
        http.formLogin()
                .loginPage(LOGIN_PAGE_URL)
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .successHandler(loginResultHandler())
                .failureHandler(loginResultHandler());
    }

    private void headers(HttpSecurity http) throws Exception {
        if (csrfProtectionEnabled) {
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        } else {
            http.csrf().disable();
        }
        http.headers().frameOptions().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

}
