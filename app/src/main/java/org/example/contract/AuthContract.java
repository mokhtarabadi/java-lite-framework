/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
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

    boolean doesUserHaveRole(UUID uuid, AuthorizationRole role) throws SQLException;
}
