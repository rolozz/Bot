package com.cor.managementservice.mappers;

import com.cor.managementservice.dto.RequestToDataBase;
import com.cor.managementservice.entities.Subscribe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface SubscribeMapper {


    @Mapping(target = "username", source = "username")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "city", ignore = true)
    Subscribe toEntity(RequestToDataBase request);
}
