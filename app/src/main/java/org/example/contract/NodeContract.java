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
import org.example.dto.DataTableDTO;
import org.example.dto.DataTableRequestDTO;
import org.example.dto.NodeDto;
import org.example.entity.Node;
import org.example.state.NodeState;

public interface NodeContract {

    NodeState addNewNode(NodeDto nodeDto) throws SQLException;

    NodeState updateNode(UUID uuid, NodeDto nodeDto) throws SQLException;

    Node getNode(UUID uuid) throws SQLException;

    NodeState deleteNode(UUID uuid) throws SQLException;

    DataTableDTO<NodeDto> fetchNodesForDataTable(DataTableRequestDTO dto) throws SQLException;
}
