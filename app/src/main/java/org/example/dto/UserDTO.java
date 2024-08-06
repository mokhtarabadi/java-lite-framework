/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UserDTO {

    private UUID id;

    @NotEmpty(message = "{signup.username.empty}")
    @Size(min = 3, max = 255, message = "{signup.username.size}")
    private String username;

    @Size(max = 255, message = "{signup.email.size}")
    @Email(message = "{email.notValid}")
    private String email;

    @NotEmpty(message = "{signup.firstName.empty}")
    @Size(min = 2, max = 30, message = "{signup.firstName.size}")
    @SerializedName("first_name")
    private String firstName;

    @Size(max = 30, message = "{signup.lastName.size}")
    @SerializedName("last_name")
    private String lastName;

    @Size(max = 30, message = "{admin.users.newPassword.size}")
    @SerializedName("new_password")
    private String newPassword;

    @Size(max = 30, message = "{admin.users.confirmNewPassword.size}")
    @SerializedName("confirm_new_password")
    private String confirmNewPassword;

    @NotBlank(message = "{signup.password.empty}")
    @Size(min = 8, max = 30, message = "{signup.password.size}")
    private String password;

    @NotBlank(message = "{signup.confirmPassword.empty}")
    @Size(min = 8, max = 30, message = "{signup.confirmPassword.size}")
    @SerializedName("confirm_password")
    private String confirmPassword;

    @SerializedName("class")
    @Min(value = 0, message = "{clazz.min}")
    private int clazz;

    // custom validator
    @AssertTrue(message = "{admin.users.confirmNewPassword.empty}")
    private boolean isNeedConfirmPassword() {
        return StringUtils.isEmpty(newPassword) || StringUtils.isNotEmpty(confirmNewPassword);
    }

    @AssertTrue(message = "{signup.passwordsDoNotMatch}")
    private boolean isPasswordsMatch() {
        return StringUtils.isEmpty(newPassword) || StringUtils.equals(newPassword, confirmNewPassword);
    }

    @AssertTrue(message = "{signup.passwordsDoNotMatch}")
    private boolean isInitialPasswordsMatch() {
        return StringUtils.isEmpty(password) || StringUtils.equals(password, confirmPassword);
    }

    // below not need when validation
    @NotEmpty(message = "{admin.users.roles.empty}")
    private List<String> roles;

    @SerializedName("is_active")
    private boolean isActive;

    @Null
    @SerializedName("created_at")
    private Date createdAt;

    @Null
    @SerializedName("updated_at")
    private Date updatedAt;
}
