/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.service;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.UUID;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.LoadingCache;
import org.example.contract.NodeContract;
import org.example.dto.DataTableDTO;
import org.example.dto.DataTableRequestDTO;
import org.example.dto.NodeDto;
import org.example.entity.Node;
import org.example.mapper.NodeMapper;
import org.example.repository.NodeRepository;
import org.example.repository.UserRepository;
import org.example.state.NodeState;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProviderService implements NodeContract {

    @NonNull private ConnectionSource connectionSource;

    @NonNull private UserRepository userRepository;

    @NonNull private NodeRepository nodeRepository;

    @NonNull private LoadingCache<UUID, Node> nodeCache;

    private final LoadingCache.CacheLoader<UUID, Node> nodeCacheLoader = new LoadingCache.CacheLoader<>() {
        @Override
        public Node load(UUID key) throws SQLException {
            return nodeRepository.read(key);
        }
    };

    @Override
    public NodeState create(NodeDto nodeDto) throws SQLException {
        return null;
    }

    @Override
    public NodeState update(UUID uuid, NodeDto nodeDto) throws SQLException {
        Node oldNode = get(uuid);

        return TransactionManager.callInTransaction(connectionSource, () -> {
            // check address exists
            if (nodeRepository.findByAddress(nodeDto.getAddress()).isPresent()) {
                return NodeState.builder().state(NodeState.State.ADDRESS_EXISTS).build();
            }

            NodeMapper.INSTANCE.updateFromDto(oldNode, nodeDto);
            nodeRepository.update(oldNode);
            nodeCache.remove(uuid);

            return NodeState.builder().state(NodeState.State.SUCCESS).build();
        });
    }

    @Override
    public Node get(UUID uuid) throws SQLException {
        return nodeCache.get(uuid, nodeCacheLoader);
    }

    @Override
    public NodeState delete(UUID uuid) throws SQLException {
        return null;
    }

    @Override
    public DataTableDTO<Node> fetchForDataTable(DataTableRequestDTO dto) throws SQLException {
        return null;
    }
}
