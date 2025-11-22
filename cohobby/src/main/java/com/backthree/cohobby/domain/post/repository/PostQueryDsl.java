package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface PostQueryDsl {

    List<Post> getPosts(
            Predicate predicate
    );
}
