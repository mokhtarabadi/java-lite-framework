/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.state;

import java.util.UUID;
import lombok.*;

@Builder
@Data
public class LoginState {

    public enum State {
        SUCCESS,
        USER_NOT_FOUND,
        INVALID_CREDENTIALS
    }

    @NonNull private State state;

    private UUID uuid;
}
