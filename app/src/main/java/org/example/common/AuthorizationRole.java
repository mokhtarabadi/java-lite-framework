/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import lombok.Getter;

@Getter
public enum AuthorizationRole {
    ROLE_ADMIN("admin"),
    ROLE_USER("user"),
    ROLE_CUSTOMER("customer"),
    ROLE_PROVIDER("provider");

    private final String role;

    AuthorizationRole(String role) {
        this.role = role;
    }

    public static AuthorizationRole from(String role) {
        switch (role) {
            case "admin":
                return ROLE_ADMIN;
            case "user":
                return ROLE_USER;
            case "customer":
                return ROLE_CUSTOMER;
            case "provider":
                return ROLE_PROVIDER;
            default:
                return null;
        }
    }
}
