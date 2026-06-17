package com.cl.mdd.server.core.security.token;

public class ChangeUsernameTokenOutput {

    private String userId;

    private String newUsername;

    public ChangeUsernameTokenOutput(String userId, String newUsername) {
        this.userId = userId;
        this.newUsername = newUsername;
    }

    public String getUserId() {
        return userId;
    }

    public String getNewUsername() {
        return newUsername;
    }

}
