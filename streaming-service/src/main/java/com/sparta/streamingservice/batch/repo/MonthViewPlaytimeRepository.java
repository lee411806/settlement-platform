package com.sparta.streamingservice.batch.repo;

import com.sparta.streamingservice.batch.entity.MonthViewPlaytime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthViewPlaytimeRepository extends JpaRepository<MonthViewPlaytime, Long> {
}
