/* (C) 2023 */
package org.example.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Data
public class UserDTO {

    private UUID id;

    @NotEmpty(message = "{signup.username.empty}")
    @Size(min = 3, max = 255, message = "{signup.username.size}")
    private String username;

    @Email(message = "{email.notValid}")
    @Size(max = 255, message = "{signup.email.size}")
    private String email;

    @NotEmpty(message = "{signup.firstName.empty}")
    @Size(min = 2, max = 30, message = "{signup.firstName.size}")
    @SerializedName("first_name")
    private String firstName;

    @Size(max = 30, message = "{signup.lastName.size}")
    @SerializedName("last_name")
    private String lastName;

    @NotBlank(message = "{signup.password.empty}")
    @Size(min = 8, max = 30, message = "{signup.password.size}")
    private String password;

    @NotBlank(message = "{signup.confirmPassword.empty}")
    @Size(min = 8, max = 30, message = "{signup.confirmPassword.size}")
    @SerializedName("confirm_password")
    private String confirmPassword;

    // custom validator
    @AssertTrue(message = "{signup.passwordsDoNotMatch}")
    private boolean isPasswordsMatch() {
        return StringUtils.isEmpty(password) || StringUtils.equals(password, confirmPassword);
    }

    // below not need when validation
    @SerializedName("is_active")
    private boolean isActive;

    @Null
    private List<String> roles;

    @SerializedName("class")
    private int clazz;

    @Null
    @SerializedName("created_at")
    private Date createdAt;

    @Null
    @SerializedName("updated_at")
    private Date updatedAt;
}
