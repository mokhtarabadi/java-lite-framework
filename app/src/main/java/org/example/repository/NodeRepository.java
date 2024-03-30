/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.repository;

import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;
import java.util.Optional;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.Node;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class NodeRepository extends AbstractCrudRepository<Node> {

    public Optional<Node> findByAddress(String address) throws SQLException {
        return getDao().queryForEq("address", new SelectArg(address)).stream().findFirst();
    }
}
