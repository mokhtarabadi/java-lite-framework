/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.state;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class NodeState {

    public enum State {
        SUCCESS,
        ADDRESS_EXISTS,
    }

    @NonNull private NodeState.State state;

    private UUID uuid;
}
