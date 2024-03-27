/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
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

}
