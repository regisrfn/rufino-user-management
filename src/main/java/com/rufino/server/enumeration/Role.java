package com.rufino.server.enumeration;

import static com.rufino.server.constant.AuthorityConst.USER_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.HR_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.MANAGER_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.ADMIN_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.SUPER_USER_AUTHORITIES;

public enum Role {

    ROLE_USER(USER_AUTHORITIES), ROLE_HR(HR_AUTHORITIES), ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES), ROLE_SUPER_ADMIN(SUPER_USER_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }

}
