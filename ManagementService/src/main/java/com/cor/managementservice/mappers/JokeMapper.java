package com.cor.managementservice.mappers;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.entities.Joke;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface JokeMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "name",source = "name")
    @Mapping(target = "isActive",source = "isActive")
    @Mapping(target = "version",ignore = true)
    Joke toEntity(JokeDto jokeDto);

    List<Joke> toEntityList(List<JokeDto> jokeDtoList);

}
