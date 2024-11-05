package com.sparta.streamingservice.batch.settlementbatch;

import com.sparta.streamingservice.batch.entity.Statistics;
import com.sparta.streamingservice.batch.repo.StatisticsRepository;
import com.sparta.streamingservice.entity.DailyVideoView;
import com.sparta.streamingservice.repository.DailyVideoViewRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SettlementBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DailyVideoViewRepository dailyVideoViewRepository;
    private final StatisticsRepository statisticsRepository;

    public SettlementBatch(JobRepository jobRepository,
                           PlatformTransactionManager platformTransactionManager,
                           DailyVideoViewRepository dailyVideoViewRepository,
                           StatisticsRepository statisticsRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.dailyVideoViewRepository = dailyVideoViewRepository;
        this.statisticsRepository = statisticsRepository;
    }

    // 통계 Job 구성
    @Bean
    public Job statisticsJob() {
        return new JobBuilder("statisticsJob", jobRepository)
                .start(settlementStep(null)) // Step에 파라미터 전달
                .build();
    }

    // Step 구성: 1일, 1주일, 1달 통계 처리
    @Bean
    @JobScope
    public Step settlementStep(@Value("#{jobParameters['period']}") String period) {
        return new StepBuilder("settlementStep", jobRepository)
                .<DailyVideoView, Statistics>chunk(10, platformTransactionManager) // Chunk 크기 3000
                .reader(videoViewReader(period)) // Reader로 데이터를 읽음
                .processor(statisticsProcessor(period)) // Processor에서 데이터를 처리함
                .writer(statisticsWriter()) // Writer에서 통계 데이터를 저장
                .build();
    }

    // Reader: 기간에 맞는 데이터를 JPA로 읽어옴
    @Bean
    @StepScope
    public ListItemReader<DailyVideoView> videoViewReader(@Value("#{jobParameters['period']}") String period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = calculateStartDate(period, today);
        LocalDate endDate = calculateEndDate(period, today);

        // 조회수 상위 5개 데이터를 읽어옴
        List<DailyVideoView> top5ByViews = dailyVideoViewRepository
                .findTop5ByDateBetweenOrderByViewCountDesc(startDate, endDate);

        // 재생 시간이 긴 상위 5개 데이터를 읽어옴
        List<DailyVideoView> top5ByPlaytime = dailyVideoViewRepository
                .findTop5ByDateBetweenOrderByPlayTimeDesc(startDate, endDate);

        // 두 개의 리스트를 결합하여 반환
        List<DailyVideoView> combinedList = new ArrayList<>();

        // 조회수 기준 데이터를 처리할 때 statType을 "view_count"로 설정
        for (DailyVideoView videoView : top5ByViews) {
            videoView.setStatType("view_count"); // 조회수 통계로 설정
            combinedList.add(videoView);
        }

        // 재생시간 기준 데이터를 처리할 때 statType을 "play_time"으로 설정
        for (DailyVideoView videoPlayTime : top5ByPlaytime) {
            videoPlayTime.setStatType("play_time"); // 재생 시간 통계로 설정
            combinedList.add(videoPlayTime);
        }

        // 조회된 데이터를 로그로 출력
        System.out.println("Reader - Period: " + period + ", StartDate: " + startDate + ", EndDate: " + endDate);
        System.out.println("Top 5 By Views: " + top5ByViews);
        System.out.println("Top 5 By Playtime: " + top5ByPlaytime);


        return new ListItemReader<>(combinedList);
    }

    private LocalDate calculateStartDate(String period, LocalDate today) {
        // 하루 단위 통계의 경우 오늘 날짜만 반환
        if ("day".equals(period)) {
            return today;
        }
        // 주간 단위 통계: 이번 주 월요일부터 시작
        else if ("week".equals(period)) {
            return today.with(DayOfWeek.MONDAY);
        }
        // 월간 단위 통계: 이번 달의 첫날부터 시작
        else if ("month".equals(period)) {
            return today.with(TemporalAdjusters.firstDayOfMonth());
        }
        // 기본값으로 오늘 날짜 반환
        return today;
    }

    private LocalDate calculateEndDate(String period, LocalDate today) {
        // 하루 단위 통계의 경우 오늘 날짜만 반환
        if ("day".equals(period)) {
            return today;
        }
        // 주간 단위 통계: 이번 주 일요일까지 종료
        else if ("week".equals(period)) {
            return today.with(DayOfWeek.SUNDAY);
        }
        // 월간 단위 통계: 이번 달의 마지막 날까지 종료
        else if ("month".equals(period)) {
            return today.with(TemporalAdjusters.lastDayOfMonth());
        }
        // 기본값으로 오늘 날짜 반환
        return today;
    }

    @Bean
    @StepScope
    public ItemProcessor<DailyVideoView, Statistics> statisticsProcessor(@Value("#{jobParameters['period']}") String period) {
        return dailyVideoView -> {
            Statistics statistics = new Statistics();
            statistics.setVideoId(dailyVideoView.getVideoId());
            statistics.setPeriodType(period); // 기간 설정

            // 조회수와 재생 시간을 구분하여 처리
            if (dailyVideoView.getViewCount() != null && dailyVideoView.getViewCount() > 0) {
                statistics.setStatType("view_count"); // 조회수 통계로 설정
                statistics.setViewCount(dailyVideoView.getViewCount());
                System.out.println("Processor - VideoId: " + dailyVideoView.getVideoId() + ", StatType: view_count, ViewCount: " + dailyVideoView.getViewCount());
            }

            if (dailyVideoView.getPlayTime() != null && dailyVideoView.getPlayTime() > 0) {
                statistics.setStatType("play_time"); // 재생 시간 통계로 설정
                statistics.setPlayTime(dailyVideoView.getPlayTime());
                System.out.println("Processor - VideoId: " + dailyVideoView.getVideoId() + ", StatType: play_time, PlayTime: " + dailyVideoView.getPlayTime());
            }

            return statistics;
        };
    }

    // Writer: 처리된 통계 데이터를 저장
    @Bean
    public RepositoryItemWriter<Statistics> statisticsWriter() {
        return new RepositoryItemWriterBuilder<Statistics>()
                .repository(statisticsRepository)
                .methodName("save")
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Statistics> statisticsWriterWithLog() {
        return items -> {
            // 저장될 데이터를 로그로 출력
            System.out.println("Writer - Saving Statistics: " + items);
            statisticsRepository.saveAll(items);
        };
    }
}
