/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;

@Getter
public enum AuthorizationRole {
    @SerializedName("admin")
    ROLE_ADMIN("admin"),
    @SerializedName("user")
    ROLE_USER("user"),
    @SerializedName("customer")
    ROLE_CUSTOMER("customer"),
    @SerializedName("provider")
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

    public static List<AuthorizationRole> all() {
        return List.of(ROLE_ADMIN, ROLE_USER, ROLE_CUSTOMER, ROLE_PROVIDER);
    }
}
