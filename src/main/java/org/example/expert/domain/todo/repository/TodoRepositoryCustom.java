package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {

    Optional<Todo> findByIdWithUser(Long todoId);

    // 검색 메서드
    Page<TodoSearchResponse> searchTodos(
            String title, // 제목 키워드 (부분일치)
            LocalDateTime startDate, // 생성일 시작
            LocalDateTime endDate, // 생성일 끝
            String nickname, // 담당자 닉네임 (부분일치)
            Pageable pageable // 페이징
    );

}
