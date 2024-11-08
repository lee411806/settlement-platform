package com.sparta.streamingservice.batch.repo;

import com.sparta.streamingservice.batch.entity.WeekViewPlaytime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekViewPlaytimeRepository extends JpaRepository<WeekViewPlaytime, Long> {
}
