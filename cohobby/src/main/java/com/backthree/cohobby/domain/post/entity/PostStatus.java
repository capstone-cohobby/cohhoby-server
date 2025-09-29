package com.backthree.cohobby.domain.post.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {
    DRAFT ("DRAFT"),
    PUBLISHED ("PUBLISHED"),
    ARCHIVED("ARCHIVED");
    private final String value;
}
