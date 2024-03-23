/* (C) 2024 */
package org.example.contract;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
import org.example.dto.DataTableDTO;
import org.example.dto.DataTableRequestDTO;
import org.example.dto.LogDTO;
import org.example.entity.Log;

public interface LogContract {
    UUID makeLog(Log.Type type, Pair<String, Object>... pairs) throws SQLException;

    DataTableDTO<LogDTO> fetchLogsForDataTable(DataTableRequestDTO dto, Log.Type... types) throws SQLException;

    List<LogDTO> getLogsPerTypeAndUser(UUID userId, int limit, Log.Type... types) throws SQLException;

    void deleteLog(UUID id) throws SQLException;
}
