/* (C) 2024 */
package org.example.mapper;

import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import org.example.dto.LogDTO;
import org.example.entity.Log;
import org.example.util.Utility;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogMapper {

    LogMapper INSTANCE = Mappers.getMapper(LogMapper.class);

    LogDTO mapFromEntity(Log entity);

    default Log mapFromRawQuery(String[] values) {
        Log log = new Log();
        log.setId(UUID.fromString(values[0]));
        log.setType(Log.Type.fromValue(values[1]));
        log.setData(Utility.getInstance().toMap(values[2]));
        log.setCreatedAt(new Date(Long.parseLong(values[3])));

        if (ObjectUtils.isNotEmpty(values[4])) {
            log.setUpdatedAt(new Date(Long.parseLong(values[4])));
        }
        return log;
    }
}
