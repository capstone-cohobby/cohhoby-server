package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.QPost;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostQueryDslImpl implements PostQueryDsl {

    private final EntityManager em;

    //검색 api
    @Override
    public List<Post> getPosts(
            Predicate predicate
    ){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QPost post = QPost.post;
        return queryFactory
                .selectFrom(post)
                .where(predicate)
                .fetch();
    }
}
