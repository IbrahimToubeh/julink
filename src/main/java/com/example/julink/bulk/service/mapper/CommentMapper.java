package com.example.julink.bulk.service.mapper;

import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentMapper {


        @Mapping(source = "commenter.id", target = "commenterId")
        @Mapping(source = "commenter.username", target = "commenterUsername")
        @Mapping(source = "post.id", target = "postId")
        CommentDto toDto(Comment comment);

}

