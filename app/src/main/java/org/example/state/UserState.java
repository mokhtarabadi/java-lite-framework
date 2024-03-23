/* (C) 2023 */
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
