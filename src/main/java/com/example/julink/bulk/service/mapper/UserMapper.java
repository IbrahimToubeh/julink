package com.example.julink.bulk.service.mapper;


import com.example.julink.bulk.dto.CommentDto;
import com.example.julink.bulk.dto.PostDto;
import com.example.julink.bulk.dto.UserProfileDto;
import com.example.julink.entryrelated.entity.Users;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PostMapper.class, CommentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected com.example.julink.bulk.repositories.PostRepo postRepo;

    @Autowired
    protected com.example.julink.bulk.repositories.CommentRepo commentRepo;

    @Autowired
    protected PostMapper postMapper;

    @Autowired
    protected CommentMapper commentMapper;

    @Mapping(target = "collegeId", source = "college.id")
    @Mapping(target = "collegeName", source = "college.name")
    @Mapping(target = "posts", expression = "java(mapPosts(user))")
    @Mapping(target = "comments", expression = "java(mapComments(user))")
    @Mapping(target = "followingIds", expression = "java(user.getFollowing().stream().map(u -> u.getId()).toList())")
    @Mapping(target = "followerIds", expression = "java(user.getFollowers().stream().map(u -> u.getId()).toList())")
    public abstract UserProfileDto toDto(Users user);

    protected List<PostDto> mapPosts(Users user) {
        return postRepo.findByAuthorId(user.getId())
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    protected List<CommentDto> mapComments(Users user) {
        return commentRepo.findByCommenterId(user.getId())
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }
}

