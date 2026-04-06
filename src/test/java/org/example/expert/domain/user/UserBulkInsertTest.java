package org.example.expert.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
public class UserBulkInsertTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 유저_500만건_bulk_insert() {
        // 한번에 insert할 배치 크기
        int batchSize = 1000;
        // 총 데이터 수
        int totalCount = 5_000_000;

        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 1; i <= totalCount; i++) {

            // UUID로 닉네임 생성 (중복 방지)
            String nickname = "user_" + UUID.randomUUID();
            String email = "user" + i + "@test.com";
            // 테스트용 비밀번호
            String password = "$2a$10$test";
            String userRole = "USER";

            batchArgs.add(new Object[]{email, nickname, password, userRole});

            // batchSize마다 한번에 insert
            if(i % batchSize == 0) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO users (email, nickname, password, user_role) VALUES (?, ?, ?, ?)",
                        batchArgs
                );
                batchArgs.clear();

                // 진행상황 출력
                System.out.println(i + "건 완료");
            }
        }
        // 남은 데이터 insert
        if(!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO users (email, nickname, password, user_role) VALUES (?, ?, ?, ?)",
                    batchArgs
            );
        }

        System.out.println("500만건 insert 완료");
    }
}
