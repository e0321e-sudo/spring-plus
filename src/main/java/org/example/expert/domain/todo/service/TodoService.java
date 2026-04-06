package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather,
                                       LocalDateTime startDate, LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos;

        if (weather != null && startDate != null && endDate != null) {
            // weather + 시작일 + 종료일
            todos = todoRepository.findByWeatherAndDateRange(weather, startDate, endDate, pageable);
        } else if (weather != null && startDate != null) {
            // weather + 시작일
            todos = todoRepository.findByWeatherAndStartDate(weather, startDate, pageable);

        } else if (weather != null && endDate != null) {
            // weather + 종료일
            todos = todoRepository.findByWeatherAndEndDate(weather, endDate, pageable);

        } else if (weather != null) {
            // weather만
            todos = todoRepository.findByWeather(weather, pageable);

        } else if (startDate != null && endDate != null) {
            // 시작일 + 종료일
            todos = todoRepository.findByDateRange(startDate, endDate, pageable);

        } else if (startDate != null) {
            // 시작일만
            todos = todoRepository.findByStartDate(startDate, pageable);

        } else if (endDate != null) {
            // 종료일만
            todos = todoRepository.findByEndDate(endDate, pageable);

        } else {
            // 조건 없으면 전체 조회
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }


        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    // 검색 메서드
    public Page<TodoSearchResponse> searchTodos(
            int page,
            int size,
            String title,              // 제목 키워드
            LocalDateTime startDate,   // 생성일 시작
            LocalDateTime endDate,     // 생성일 끝
            String nickname            // 담당자 닉네임
    ) {
        // 페이지 설정 (1페이지부터 시작하도록 -1)
        Pageable pageable = PageRequest.of(page -1, size);

        // Repository의 QueryDSL 검색 메서드 호출
        return todoRepository.searchTodos(
                title, startDate, endDate, nickname, pageable);
    }
}
