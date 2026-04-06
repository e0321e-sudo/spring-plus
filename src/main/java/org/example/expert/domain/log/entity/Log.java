package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 요청인지 기록
    private String message;

    // 로그 생성 시간
    private LocalDateTime createdAt;

    public Log(String message) {
        this.message = message;
        // 로그 저장할 때 자동으로 현재 시간 기록
        this.createdAt = LocalDateTime.now();
    }
}
