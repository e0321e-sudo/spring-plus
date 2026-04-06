package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo todo = queryFactory
                .selectFrom(QTodo.todo)
                .leftJoin(QTodo.todo.user, QUser.user).fetchJoin()
                .where(QTodo.todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(todo);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String nickname,
            Pageable pageable
    ) {
        // Q클래스 변수 선언 (각 엔티티의 별명)
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        // 조건이 있을 때만 바구니 담기
        BooleanBuilder builder = new BooleanBuilder();

        // 제목 키워드 있을 때만 조건 추가
        if(title != null && !title.isEmpty()) {
            builder.and(todo.title.contains(title));
        }

        // 생성일 시작 조건 있을 때만 추가
        if(startDate != null) {
            builder.and(todo.createdAt.goe(startDate));
        }

        // 생성일 종료 조건 있을 때만 추가
        if(endDate != null) {
            builder.and(todo.createdAt.loe(endDate));
        }

        // 닉네임 조건 있을 때만 추가
        if(nickname != null && !nickname.isEmpty()) {
            builder.and(manager.user.nickname.contains(nickname));
        }

        // 실제 데이터 조회
        List<TodoSearchResponse> results = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,         // 제목
                        manager.count(),    // 담당자 수
                        comment.count()     // 댓글 수
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(builder)
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 수 조회 (페이징에 필요)
        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(builder)
                .fetchOne();

        // 페이징 결과 객체로 감싸서 반환
        return new PageImpl<>(results, pageable, total != null ? total : 0);


    }



}
