/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

@Data
public class DataTableDTO<T> {

    private int draw;

    @SerializedName("recordsTotal")
    private long recordsTotal;

    @SerializedName("recordsFiltered")
    private long recordsFiltered;

    private List<T> data;
}
