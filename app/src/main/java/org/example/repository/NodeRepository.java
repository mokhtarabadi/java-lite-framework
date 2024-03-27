package org.example.repository;

import com.j256.ormlite.stmt.SelectArg;
import lombok.NoArgsConstructor;
import org.example.entity.Node;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Optional;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class NodeRepository extends AbstractCrudRepository<Node> {

    public Optional<Node> findByAddress(String address) throws SQLException {
        return getDao().queryForEq("address", new SelectArg(address)).stream().findFirst();
    }

}
