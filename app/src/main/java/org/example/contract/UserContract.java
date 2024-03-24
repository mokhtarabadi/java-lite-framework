/* (C) 2024 */
package org.example.contract;

import java.sql.SQLException;
import java.util.UUID;
import org.example.dto.*;
import org.example.entity.User;
import org.example.state.UserState;

public interface UserContract {
    DataTableDTO<UserDTO> fetchUsersForDataTable(DataTableRequestDTO dto) throws SQLException;

    UserState deleteUser(UUID uuid) throws SQLException;

    UserState updateUser(UUID uuid, UpdateUserDTO dto) throws SQLException;

    UserState addNewUser(UserDTO dto) throws SQLException;

    User getUser(UUID uuid) throws SQLException;
}
