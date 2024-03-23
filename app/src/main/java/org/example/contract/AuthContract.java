/* (C) 2024 */
package org.example.contract;

import java.sql.SQLException;
import java.util.UUID;
import org.example.common.AuthorizationRole;
import org.example.dto.LoginDTO;
import org.example.state.LoginState;

public interface AuthContract {
    LoginState validateUserCredentials(LoginDTO dto) throws SQLException;

    void assignRoleToUser(UUID uuid, AuthorizationRole role) throws SQLException;

    void revokeRoleFromUser(UUID uuid, AuthorizationRole role) throws SQLException;

    boolean doesUserHaveRole(UUID uuid, AuthorizationRole role);
}
