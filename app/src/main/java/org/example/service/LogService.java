/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.service;

import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.contract.LogContract;
import org.example.dto.DataTableDTO;
import org.example.dto.DataTableRequestDTO;
import org.example.dto.LogDTO;
import org.example.entity.Log;
import org.example.mapper.LogMapper;
import org.example.repository.LogRepository;
import org.jetbrains.annotations.NotNull;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class LogService implements LogContract {

    @NonNull ConnectionSource connectionSource;

    @NonNull private LogRepository logRepository;

    @SafeVarargs
    @Override
    public final UUID makeLog(Log.Type type, @NotNull Pair<String, Object>... pairs) throws SQLException {
        Log log = new Log();
        log.setType(type);

        Map<String, Object> map = new HashMap<>();
        for (Pair<String, Object> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        log.setData(map);

        return logRepository.create(log);
    }

    @Override
    public DataTableDTO<LogDTO> fetchLogsForDataTable(DataTableRequestDTO dto, Log.Type... types) throws SQLException {
        DataTableDTO<LogDTO> dataTableDTO = new DataTableDTO<>();
        dataTableDTO.setRecordsTotal(logRepository.countByTypes(types));
        dataTableDTO.setDraw(dto.getDraw());

        Pair<String, Pair<String, Object[]>> type = Pair.of("in", Pair.of("type", types));

        List<Log> searchLogs = logRepository.queryForDataTable(
                dto.getStart(),
                dto.getLength(),
                dto.getColumns(),
                dto.getSearch().getValue(),
                dto.getOrder().get(0).getColumn(),
                dto.getOrder().get(0).getDir(),
                type);

        dataTableDTO.setRecordsFiltered(logRepository.countForDataTable(
                dto.getColumns(), dto.getSearch().getValue(), type));

        dataTableDTO.setData(
                searchLogs.stream().map(LogMapper.INSTANCE::mapFromEntity).collect(Collectors.toList()));
        return dataTableDTO;
    }

    @Override
    public void deleteLog(UUID id) throws SQLException {
        logRepository.deleteById(id);
    }
}
