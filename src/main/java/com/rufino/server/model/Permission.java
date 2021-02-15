package com.rufino.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Authority authority;

    public String getAuthority() {
        return authority.toString();
    }

    public void setAuthority(String authority) {
        try {
            this.authority = Authority.valueOf(authority);
        } catch (Exception e) {
            this.authority = null;
        }

    }

    private enum Authority {
        WRITE, READ, DELETE, UPDATE
    }
}
