/* (C) 2024 */
package org.example.contract;

import java.sql.SQLException;

public interface SystemConfigContract {
    String getConfigByKey(String key) throws SQLException;

    void writeConfig(String key, String value) throws SQLException;
}
