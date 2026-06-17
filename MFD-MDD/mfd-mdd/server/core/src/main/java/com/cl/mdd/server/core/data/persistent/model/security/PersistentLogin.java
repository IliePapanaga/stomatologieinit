package com.cl.mdd.server.core.data.persistent.model.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "PERSISTENT_LOGINS")
public class PersistentLogin implements Serializable {

    @Id
    @Column(name = "series", nullable = false)
    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private ZonedDateTime lastUsed;

    public String getName() {
        return name;
    }

    public PersistentLogin setName(String name) {
        this.name = name;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PersistentLogin setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getToken() {
        return token;
    }

    public PersistentLogin setToken(String token) {
        this.token = token;
        return this;
    }

    public ZonedDateTime getLastUsed() {
        return lastUsed;
    }

    public PersistentLogin setLastUsed(ZonedDateTime lastUsed) {
        this.lastUsed = lastUsed;
        return this;
    }
}
