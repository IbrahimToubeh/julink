package com.example.julink.bulk.service.mapper;


import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.dto.UserProfileDto;
import com.example.julink.entryrelated.entity.Users;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PostMapper.class, CommentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected com.example.julink.bulk.repositories.PostRepo postRepo;

    @Autowired
    protected com.example.julink.bulk.repositories.CommentRepo commentRepo;

    @Autowired
    protected com.example.julink.bulk.service.mapper.PostMapper postMapper;

    @Autowired
    protected CommentMapper commentMapper;

    @Mapping(target = "posts", expression = "java(mapPosts(user))")
    @Mapping(target = "comments", expression = "java(mapComments(user))")
    public abstract UserProfileDto toDto(Users user);

    protected List<PostDto> mapPosts(Users user) {
        return postRepo.findByAuthorId(user.getId())
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    protected List<CommentDto> mapComments(Users user) {
        return commentRepo.findByCommenterId(user.getId())  // fixed here
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

}
