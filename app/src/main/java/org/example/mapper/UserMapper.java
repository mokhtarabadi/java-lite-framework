/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.mapper;

import org.example.dto.UpdateUserDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "createdAt", ignore = true) // generated using reflection
    @Mapping(target = "userRoles", ignore = true) // automatically must-fill
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clazz", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
    User mapFromDTO(UserDTO dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    @Mapping(
            target = "roles",
            expression =
                    "java(entity.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).collect(java.util.stream.Collectors.toList()))")
    UserDTO mapFromEntity(User entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
    void updateFromDTO(@MappingTarget User entity, UpdateUserDTO dto);
}
