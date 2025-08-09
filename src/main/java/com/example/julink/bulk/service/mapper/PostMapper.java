package com.example.julink.bulk.service.mapper;

import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.entity.College;
import com.example.julink.bulk.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "taggedColleges", target = "taggedCollegeIds", qualifiedByName = "mapCollegeIds")
    PostDto toDto(Post post);

    @Named("mapCollegeIds")
    default List<Long> mapCollegeIds(Set<College> colleges) {
        if (colleges == null) return Collections.emptyList();
        return colleges.stream()
                .map(College::getId)
                .collect(Collectors.toList());
    }
}
