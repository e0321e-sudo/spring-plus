package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // weather만 있을 때
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH  t.user u " +
           "WHERE t.weather = :weather " +
           "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeather(@Param("weather") String weather, Pageable pageable);

    // 시작일만 있을 때
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.modifiedAt >= :startDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByStartDate(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    // 종료일만 있을 때
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.modifiedAt <= :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByEndDate(@Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 시작일 + 종료일
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.modifiedAt >= :startDate AND t.modifiedAt <= :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    //  weather + 시작일
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.weather = :weather AND t.modifiedAt >= :startDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndStartDate(
            @Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    //  weather + 종료일
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.weather = :weather AND t.modifiedAt <= :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndEndDate(
            @Param("weather") String weather,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    //  weather + 시작일 + 종료일
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.weather = :weather " +
            "AND t.modifiedAt >= :startDate AND t.modifiedAt <= :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndDateRange(
            @Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
