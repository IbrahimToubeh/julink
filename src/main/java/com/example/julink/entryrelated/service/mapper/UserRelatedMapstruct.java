package com.example.julink.entryrelated.service.mapper;


import com.example.julink.entryrelated.dto.OTPDTO;
import com.example.julink.entryrelated.entity.OTPClass;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRelatedMapstruct {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    Users CreateUserRequestsDTOtoEntity(CreateUserRequestDTO dto);

    Users LoginUserRequestsDTOtoEntity(LoginUserRequestDTO dto);

    @Mapping(target = "otp", source = "otp")
    OTPClass OTPDTOtoEntity(OTPDTO dto);
}
