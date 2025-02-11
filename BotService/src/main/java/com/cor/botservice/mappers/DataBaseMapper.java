package com.cor.botservice.mappers;

import com.cor.botservice.dto.RequestToDataBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper(componentModel = "Spring")
public interface DataBaseMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "username", source = "userName")
    RequestToDataBase request(User user);

}
