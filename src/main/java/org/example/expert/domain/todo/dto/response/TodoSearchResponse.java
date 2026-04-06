package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoSearchResponse {

    // 제목만 반환
    private final String title;

    // 해당 일정의 담당자 수
    private final Long managerCount;

    // 해당 일정의 총 댓글 수
    private final Long commentCount;


    @QueryProjection
    public TodoSearchResponse(String title, Long managerCount, Long commentCount) {
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
