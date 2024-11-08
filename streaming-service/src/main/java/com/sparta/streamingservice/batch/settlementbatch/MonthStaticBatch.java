package com.sparta.streamingservice.batch.settlementbatch;


import com.sparta.streamingservice.batch.entity.MonthViewPlaytime;
import com.sparta.streamingservice.batch.repo.StatisticsRepository;
import com.sparta.streamingservice.batch.repo.MonthViewPlaytimeRepository;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MonthStaticBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DailyVideoViewRepository dailyVideoViewRepository;
    private final MonthViewPlaytimeRepository monthViewPlaytimeRepository;

    public MonthStaticBatch(JobRepository jobRepository,
                            PlatformTransactionManager platformTransactionManager,
                            DailyVideoViewRepository dailyVideoViewRepository,
                            MonthViewPlaytimeRepository monthViewPlaytimeRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.dailyVideoViewRepository = dailyVideoViewRepository;
        this.monthViewPlaytimeRepository = monthViewPlaytimeRepository;
    }

    // Job 구성
    @Bean
    public Job monthlyStatisticsJob() {
        return new JobBuilder("monthlyStatisticsJob", jobRepository)
                .start(monthlyStatisticsStep())
                .build();
    }

    // Step 구성
    @Bean
    @JobScope
    public Step monthlyStatisticsStep() {
        return new StepBuilder("monthlyStatisticsStep", jobRepository)
                .<DailyVideoView, MonthViewPlaytime>chunk(100, platformTransactionManager) // chunk 크기 설정
                .reader(monthlyStatisticsReader())
                .processor(monthlyStatisticsProcessor())
                .writer(monthlyStatisticsWriter())
                .build();
    }

    // Reader 구성
    @Bean
    @StepScope
    public RepositoryItemReader<DailyVideoView> monthlyStatisticsReader() {

        // 오늘 날짜를 기준으로 해당 월의 첫 날과 마지막 날 계산
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);  // 이번 달의 첫 날
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth()); // 이번 달의 마지막 날

        return new RepositoryItemReaderBuilder<DailyVideoView>()
                .name("monthlyStatisticsReader")  // 이름 지정
                .repository(dailyVideoViewRepository)
                .methodName("findByDateBetweenOrderByVideoId")
                .arguments(startDate, endDate)
                .pageSize(100) // pageSize 설정
                .sorts(Collections.singletonMap("videoId", Sort.Direction.ASC))
                .build();
    }

    // Processor 구성
    @Bean
    public ItemProcessor<DailyVideoView, MonthViewPlaytime> monthlyStatisticsProcessor() {
        return dailyVideoView -> {
            MonthViewPlaytime monthlyStat = new MonthViewPlaytime();
            monthlyStat.setVideoId(dailyVideoView.getVideoId());
            monthlyStat.setStartDate(dailyVideoView.getDate().withDayOfMonth(1)); // 해당 월의 첫 날
            monthlyStat.setEndDate(dailyVideoView.getDate().withDayOfMonth(dailyVideoView.getDate().lengthOfMonth())); // 해당 월의 마지막 날
            monthlyStat.setTotalViewCount(dailyVideoView.getViewCount());
            monthlyStat.setTotalPlayTime(dailyVideoView.getPlayTime());

            return monthlyStat;
        };
    }

    // Writer 구성
    @Bean
    public ItemWriter<MonthViewPlaytime> monthlyStatisticsWriter() {
        return items -> {
            Map<Long, MonthViewPlaytime> mergedStats = new HashMap<>();

            for (MonthViewPlaytime item : items) {
                Long videoId = item.getVideoId();
                mergedStats.merge(videoId, item, (existing, newItem) -> {
                    existing.setTotalViewCount(existing.getTotalViewCount() + newItem.getTotalViewCount());
                    existing.setTotalPlayTime(existing.getTotalPlayTime() + newItem.getTotalPlayTime());
                    return existing;
                });
            }

            // 최종적으로 병합된 데이터를 데이터베이스에 저장
            monthViewPlaytimeRepository.saveAll(mergedStats.values());
        };
    }
}

