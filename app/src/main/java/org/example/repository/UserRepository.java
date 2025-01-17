/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.repository;

import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.User;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class UserRepository extends AbstractCrudRepository<User> {

    public Optional<User> findByUsername(String username) throws SQLException {
        return getDao().queryForEq("username", new SelectArg(username)).stream().findFirst();
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        return getDao().queryForEq("email", new SelectArg(email)).stream().findFirst();
    }
}
