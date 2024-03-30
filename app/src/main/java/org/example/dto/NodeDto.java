/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NodeDto {

    @NotEmpty(message = "{provider.node.addressEmpty}")
    private String address;
}
