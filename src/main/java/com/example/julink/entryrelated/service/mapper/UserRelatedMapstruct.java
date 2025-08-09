package com.example.julink.entryrelated.service.mapper;


import com.example.julink.bulk.entity.College;
import com.example.julink.bulk.repositories.CollegeRepository;
import com.example.julink.entryrelated.dto.OTPDTO;
import com.example.julink.entryrelated.entity.OTPClass;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.dto.CreateUserRequestDTO;
import com.example.julink.entryrelated.dto.LoginUserRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserRelatedMapstruct {

    @Autowired
    private CollegeRepository collegeRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(source = "collegeId", target = "college")
    public abstract Users CreateUserRequestsDTOtoEntity(CreateUserRequestDTO dto);

    public College mapCollege(Long collegeId) {
        if (collegeId == null) {
            return null;
        }
        return collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found with id: " + collegeId));
    }

    public abstract Users LoginUserRequestsDTOtoEntity(LoginUserRequestDTO dto);

    @Mapping(target = "otp", source = "otp")
    public abstract OTPClass OTPDTOtoEntity(OTPDTO dto);
}
