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
public class DataTableRequestDTO {

    private int draw;
    private List<Column> columns;
    private List<Order> order;
    private int start;
    private int length;
    private Search search;

    @Data
    public static class Column {
        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;
    }

    @Data
    public static class Order {
        private int column;
        private String dir;
    }

    @Data
    public static class Search {
        private String value;
        private boolean regex;
    }
}
