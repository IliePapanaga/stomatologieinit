package com.cl.mdd.server.core.service.payment.impl.primerate;

public class Credentials {

    private String login;
    private String password;
    private String apiKey;

    public Credentials() {
    }

    public Credentials(String login, String password, String apiKey) {
        this.login = login;
        this.password = password;
        this.apiKey = apiKey;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
