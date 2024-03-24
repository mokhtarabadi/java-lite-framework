/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.contract;

import java.sql.SQLException;

public interface SystemConfigContract {
    String getConfigByKey(String key) throws SQLException;

    void writeConfig(String key, String value) throws SQLException;
}
