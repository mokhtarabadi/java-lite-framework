/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.contract;

import org.example.dto.NodeDto;
import org.example.entity.Node;
import org.example.state.NodeState;

public interface NodeContract
        extends AbstractCrudContract<Node, NodeDto, NodeState>, AbstractDatatableContract<NodeDto> {}
