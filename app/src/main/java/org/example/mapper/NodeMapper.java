package org.example.mapper;

import org.example.dto.NodeDto;
import org.example.entity.Node;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NodeMapper {

    NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

    NodeDto mapFromEntity(org.example.entity.Node node);

    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Node mapFromDto(NodeDto dto);

    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateFromDto(@MappingTarget Node entity, NodeDto dto);
}
