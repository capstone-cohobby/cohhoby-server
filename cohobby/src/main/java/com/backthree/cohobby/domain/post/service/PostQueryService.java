package com.backthree.cohobby.domain.post.service;


import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.QPost;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final PostRepository postRepository;
    public List<Post> getPosts(String query, String type) {

        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        if(type.equals("category")) {
            builder.and(post.hobby.category.name.eq(query));
        } else if(type.equals("hobby")) {
            builder.and(post.hobby.name.eq(query));
        } else if(type.equals("search")) {
            builder.and(post.hobby.name.contains(query)
                    .or(post.goods.contains(query)));
        }
        List<Post> postList = postRepository.getPosts(builder);
        return postList;
    }
}
