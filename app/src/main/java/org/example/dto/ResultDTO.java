/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.dto;

import java.util.List;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private boolean success;
    private T data;
    private List<String> errors;
}
