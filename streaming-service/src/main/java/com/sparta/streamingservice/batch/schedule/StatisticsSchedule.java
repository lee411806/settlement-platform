package com.sparta.streamingservice.batch.schedule;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class StatisticsSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    public StatisticsSchedule(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    // 매번 애플리케이션 시작 후 7초 뒤에 한 번 실행
    @Scheduled(initialDelay = 7000, fixedRate = Long.MAX_VALUE)
    public void runDailyStatisticsJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("period", "day") // 1일 통계
                .addLong("time", System.currentTimeMillis()) // 유니크한 파라미터 추가
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("statisticsJob"), jobParameters);
    }

//    // 매일 자정에 1일 통계 처리
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void runDailyStatisticsJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("period", "day") // 1일 통계
//                .addLong("time", System.currentTimeMillis()) // 유니크한 파라미터 추가
//                .toJobParameters();
//        jobLauncher.run(jobRegistry.getJob("statisticsJob"), jobParameters);
//    }
//
//    // 매주 월요일 자정에 1주 통계 처리
//    @Scheduled(cron = "0 0 0 * * MON")
//    public void runWeeklyStatisticsJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("period", "week") // 1주 통계
//                .addLong("time", System.currentTimeMillis()) // 유니크한 파라미터 추가
//                .toJobParameters();
//        jobLauncher.run(jobRegistry.getJob("statisticsJob"), jobParameters);
//    }
//
//    // 매월 1일 자정에 1달 통계 처리
//    @Scheduled(cron = "0 0 0 1 * ?")
//    public void runMonthlyStatisticsJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("period", "month") // 1달 통계
//                .addLong("time", System.currentTimeMillis()) // 유니크한 파라미터 추가
//                .toJobParameters();
//        jobLauncher.run(jobRegistry.getJob("statisticsJob"), jobParameters);
//    }

}