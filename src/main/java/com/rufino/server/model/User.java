package com.rufino.server.model;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rufino.server.enumeration.Authority;
import com.rufino.server.enumeration.Role;

import lombok.Getter;
import lombok.Setter;

import static com.rufino.server.enumeration.Role.ROLE_USER;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "nickname", name = "uk_user_nickname"),
        @UniqueConstraint(columnNames = "userNo", name = "uk_user_no") })
public class User implements Serializable {
    /**
     *
     */
    @Transient
    private static final long serialVersionUID = 8092184209561160500L;

    @Id
    private UUID userId;

    @NotNull(message = "Value should not be empty")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userNo;

    @NotNull(message = "Value should not be empty")
    private String nickname, firstName, lastName, password, email; 
    
    private String imageUrl;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastLogin, lastLoginDisplay, updatedAt;

    @NotNull(message = "Value should not be empty")
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    @NotNull(message = "Value should not be empty")
    private Role role;

    @NotNull(message = "Value should not be empty")
    private boolean isActive, isLocked;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<AuthorityModel> authorityList;

    public User() {
        this.userId = UUID.randomUUID();
        this.createdAt = ZonedDateTime.now(ZoneId.of("Z"));
        this.isActive = true;
        this.isLocked = false;
        setRole(ROLE_USER);
        setAuthorityList(ROLE_USER.getAuthorities());
    }

    public List<Authority> getAuthorityList() {
        return this.authorityList.stream().map(auth -> {
            return auth.getAuthority();
        }).collect(Collectors.toList());
    }

    public void setAuthorityList(Authority... authorities) {
        List<Authority> aList = Arrays.asList(authorities);
        this.authorityList = aList.stream().map(auth -> {
            return new AuthorityModel(auth);
        }).collect(Collectors.toList());
    }
}
