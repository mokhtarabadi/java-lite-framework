/* (C) 2023 */
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
