package com.rufino.server.model;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "users", uniqueConstraints = { 
        @UniqueConstraint(columnNames = "userEmail", name = "uk_user_email"),
        @UniqueConstraint(columnNames = "userNickname", name = "uk_user_nickname"),
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
    private String nickname, firstname, lastname, password, email, imageUrl;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lastLogin,lastLoginDisplay, updatedAt;

    @NotNull(message = "Value should not be empty")
    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    @NotNull(message = "Value should not be empty")
    private Role role;

    @NotNull(message = "Value should not be empty")
    private boolean isActive, isLocked;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Permission> permissionsList;

    public User() {
        this.userId = UUID.randomUUID();
        this.createdAt = ZonedDateTime.now(ZoneId.of("Z"));
        this.isActive = true;
        this.isLocked = false;
    }

    public void setUserRole(String role) {
        try {
            this.role = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            this.role = null;
        }
    }

    public String getRole() {
        return this.role.toString();
    }

    private enum Role {
        ROLE_USER, ROLE_ADMIN, ROLE_MANAGER, ROLE_SUPER_MANAGER
    }

}
