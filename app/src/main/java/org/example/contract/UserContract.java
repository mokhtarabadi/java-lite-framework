/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.contract;

import org.example.dto.*;
import org.example.entity.User;
import org.example.state.UserState;

public interface UserContract
        extends AbstractCrudContract<User, UserDTO, UserState>, AbstractDatatableContract<UserDTO> {}
