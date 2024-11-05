package com.sparta.streamingservice.repository;


import com.sparta.streamingservice.entity.DailyVideoView;
import com.sparta.streamingservice.entity.Videos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyVideoViewRepository extends JpaRepository<DailyVideoView, Long> {

    // 특정 동영상과 날짜(today)에 해당하는 DailyVideoView 데이터를 조회 (Optional 반환)
    Optional<DailyVideoView> findByVideoIdAndDate(Long videoId, LocalDate today);

    // 주어진 날짜 범위(startDate ~ endDate) 내에서 조회수가 높은 상위 5개의 DailyVideoView 데이터를 조회 (내림차순 정렬)
    List<DailyVideoView> findTop5ByDateBetweenOrderByViewCountDesc(LocalDate startDate, LocalDate endDate);

    // 특정 날짜(playedDate)에 해당하는 모든 DailyVideoView 데이터를 조회
    List<DailyVideoView> findAllByDate(LocalDate playedDate);

    // 주어진 날짜 범위(startDate ~ endDate)에 해당하는 모든 DailyVideoView 데이터를 조회
    List<DailyVideoView> findAllByDateBetween(LocalDate startDate, LocalDate endDate);


    List<DailyVideoView> findTop5ByDateBetweenOrderByPlayTimeDesc(LocalDate startDate, LocalDate endDate);
}