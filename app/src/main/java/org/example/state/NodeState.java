package org.example.state;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Builder
@Data
public class NodeState {

    public enum State {
        SUCCESS,
        ADDRESS_EXISTS,
    }

    @NonNull
    private NodeState.State state;

    private UUID uuid;
}
