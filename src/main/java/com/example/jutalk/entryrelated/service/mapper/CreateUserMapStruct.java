package com.example.jutalk.entryrelated.service.mapper;


import com.example.jutalk.entryrelated.Users;
import com.example.jutalk.entryrelated.dto.CreateUserRequestDTO;
import com.example.jutalk.entryrelated.dto.LoginUserRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CreateUserMapStruct {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    Users CreateUserRequestsDTOtoEntity(CreateUserRequestDTO dto);

    Users LoginUserRequestsDTOtoEntity(LoginUserRequestDTO dto);
}
