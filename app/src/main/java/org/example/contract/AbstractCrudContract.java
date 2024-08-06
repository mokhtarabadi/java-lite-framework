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

public interface AbstractCrudContract<T, D, S> {
    S create(D D) throws SQLException;

    S update(UUID uuid, D D) throws SQLException;

    T get(UUID uuid) throws SQLException;

    S delete(UUID uuid) throws SQLException;
}
