/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.contract;

import java.sql.SQLException;
import org.example.dto.DataTableDTO;
import org.example.dto.DataTableRequestDTO;

public interface AbstractDatatableContract<D> {
    DataTableDTO<D> fetchForDataTable(DataTableRequestDTO dto) throws SQLException;
}
