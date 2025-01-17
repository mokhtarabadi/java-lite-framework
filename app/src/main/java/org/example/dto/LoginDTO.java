/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.dto;

import com.google.gson.annotations.SerializedName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDTO {

    @NotEmpty(message = "{login.usernameRequired}")
    private String username;

    @Email(message = "{email.notValid}")
    private String email;

    @NotEmpty(message = "{login.passwordRequired}")
    private String password;

    @SerializedName("remember_me")
    private boolean rememberMe;
}
