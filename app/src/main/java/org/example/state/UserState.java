/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.state;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class UserState {

    public enum State {
        EMAIL_TAKEN,
        USERNAME_TAKEN,
        INVALID_ROLE,
        SUCCESS
    }

    @NonNull private State state;

    private UUID uuid;
}
