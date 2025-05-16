package com.ssafy.BaeAndChoi.board.domain;

import lombok.Getter;

@Getter
public enum PostType {
    NOTICE("공지"),
    INFO("정보"),
    QUESTION("질문"),
    PROMOTION("홍보"),
    CHAT("잡담");

    private final String displayName;

    PostType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
