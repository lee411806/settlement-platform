package com.sparta.streamingservice.service;


import com.sparta.streamingservice.dto.AdviewcountRequestDto;
import com.sparta.streamingservice.dto.StatsResponse;
import com.sparta.streamingservice.entity.DailyVideoView;
import com.sparta.streamingservice.entity.ReviewCountAuthentication;
import com.sparta.streamingservice.entity.VideoViewHistory;
import com.sparta.streamingservice.entity.Videos;
import com.sparta.streamingservice.jwt.JwtUtil;
import com.sparta.streamingservice.repository.DailyVideoViewRepository;
import com.sparta.streamingservice.repository.ReviewCountAuthenticationRepository;
import com.sparta.streamingservice.repository.VideoRepository;
import com.sparta.streamingservice.repository.VideoViewHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StreamingService {

    private final VideoViewHistoryRepository videoViewHistoryRepository;

    private final VideoRepository videoRepository;

    private final ReviewCountAuthenticationRepository reviewCountAuthenticationRepository;

    private final DailyVideoViewRepository dailyVideoViewRepository;

    private final JwtUtil jwtUtil;

    //동영상 재생서비스
    //실제로 로그인 하고 play 날려서 jwtToken, ipAddress 잡히는 지 볼 것
    //반환 값 : 현재 시청중인 동영상 시점
    public int play(Long userId, Long videoId, HttpServletRequest httpServletRequest) {


        String jwtToken = jwtUtil.getJwtFromHeader(httpServletRequest);
        String ipAddress = getClientIp(httpServletRequest);

        // 실행 로그
        System.out.println("play 메서드 실행 중 - userId: " + userId + ", videoId: " + videoId);
        System.out.println("JWT Token: " + jwtToken);
        System.out.println("Client IP Address: " + ipAddress);
        // 어뷰징 체크
        if (isAbusiveRequest(jwtToken, ipAddress)) {
            System.out.println("어뷰징입니다.");

            //어뷰징은 재생시점 0으로 반환
            return 0;
        } else {

            // 재생 위치 조회 및 조회수 증가
            Optional<VideoViewHistory> historyOpt = videoViewHistoryRepository.findByUserIdAndVideoId(userId, videoId);
            if (historyOpt.isPresent()) {
                //시청 기록이 있을 때 조회수 증가
                incrementViewCount(videoId);

                // 시청 기록이 있는 경우: 마지막 재생 위치를 반환
                return historyOpt.get().getCurrentPosition();
            } else {
                // 시청 기록이 없는 경우: 새로운 시청 기록을 생성하여 반환
                int currentPosition = createNewHistory(userId, videoId);

                // 시청 기록이 없을 때 조회수 증가
                incrementViewCount(videoId);

                return currentPosition;
            }

        }


    }

    //어뷰징 방지 메서드
    public boolean isAbusiveRequest(String jwtToken, String ipAddress) {
        System.out.println("isAbusiveRequest 메서드 실행 중 - JWT Token: " + jwtToken + ", IP Address: " + ipAddress);
        Optional<ReviewCountAuthentication> reviewCountAuthentication = reviewCountAuthenticationRepository.findByJwtTokenAndIpAddress(jwtToken, ipAddress);

        int currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000);

        if (reviewCountAuthentication.isPresent()) {
            ReviewCountAuthentication authLog = reviewCountAuthentication.get();
            boolean isAbusive = (currentTimeInSeconds - authLog.getLastActionTime()) <= 30;

            // 어뷰징이 감지되면 lastActionTime 업데이트
            if (isAbusive) {
                authLog.setLastActionTime(currentTimeInSeconds);
                reviewCountAuthenticationRepository.save(authLog);
                System.out.println("어뷰징 요청 감지됨 - lastActionTime 업데이트 완료");
            }

            return isAbusive;
        } else {
            // 어뷰징 기록이 없으므로 새로 생성하여 저장하고 false 반환
            createInitialReviewCountAuthentication(jwtToken, ipAddress, currentTimeInSeconds);
            return false;
        }
    }

    // 어뷰징 기록이 없을 때 새로 생성하는 메서드
    private void createInitialReviewCountAuthentication(String jwtToken, String ipAddress, int currentTimeInSeconds) {
        ReviewCountAuthentication newAuthLog = new ReviewCountAuthentication();
        newAuthLog.setJwtToken(jwtToken);
        newAuthLog.setIpAddress(ipAddress);
        newAuthLog.setLastActionTime(currentTimeInSeconds);
        reviewCountAuthenticationRepository.save(newAuthLog);
        System.out.println("어뷰징 방지 기록 새로 생성 및 저장 완료");
    }



    // 조회 이력이 없는 경우 새로운 기록 생성
    public int createNewHistory(Long userId, Long videoId) {
        System.out.println("createNewHistory 메서드 실행 중 - userId: " + userId + ", videoId: " + videoId);
        Videos video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid video ID: " + videoId));

        VideoViewHistory newHistory = new VideoViewHistory();
        newHistory.setUserId(userId);
        newHistory.setVideo(video);
        newHistory.setCurrentPosition(0);
        newHistory.setLastPlayedDate(LocalDateTime.now());

        videoViewHistoryRepository.save(newHistory);

        // 확인용 로그
        System.out.println("VideoViewHistory 저장 완료 - userId: " + newHistory.getUserId());
        return 0; // 새로 생성되었으므로 0초 반환
    }

    // 조회수 증가 메서드
    public void incrementViewCount(Long videoId) {
        System.out.println("incrementViewCount 메서드 실행 중 - videoId: " + videoId);
//        Videos video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오입니다."));

        LocalDate today = LocalDate.now();

        // DailyVideoView에서 오늘 날짜로 된 기록이 있는지 확인
        DailyVideoView dailyVideoView = dailyVideoViewRepository.findByVideoIdAndDate(videoId, today)
                .orElseGet(() -> new DailyVideoView(videoId, today)); // 없으면 새로운 레코드 생성

        //조회수 1증가
        //incrementViewCount는 재귀가 아니라 엔티티에 있는 메서드(자바 프로그램 짜듯이 엔티티에 메서드 넣어놈)
        dailyVideoView.incrementViewCount();

        // 저장
        dailyVideoViewRepository.save(dailyVideoView);


    }

    //정지 메서드
    public void pause(Long userId, Long videoId, int currentPosition) {

        //비디오 중지 시점 저장
        VideoViewHistory history = videoViewHistoryRepository.findByUserIdAndVideoId(userId, videoId)
                .orElseThrow(() -> new EntityNotFoundException("No history found for this user and video"));

        history.setLastPlayedDate(LocalDateTime.now());
        history.setCurrentPosition(currentPosition);

        System.out.println("pause 메서드 실행 중 - userId: " + userId + ", videoId: " + videoId + ", currentPosition: " + currentPosition);

        //비디오 총 재생시간 증가
//        Videos video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오입니다."));
        LocalDate today = LocalDate.now();
        DailyVideoView dailyVideoView = dailyVideoViewRepository.findByVideoIdAndDate(videoId, today)
                .orElseThrow(() -> new IllegalStateException("시간, 비디오 총 재생 시간 테이블이 없습니다. Video ID: " + videoId + ", Date: " + today));

        // 재생시간 증가(엔티티에 증가 메서드 있음)
        dailyVideoView.increasePlaytime(currentPosition);
        dailyVideoViewRepository.save(dailyVideoView);

        videoViewHistoryRepository.save(history); // 시청 기록도 업데이트
    }

    //광고 조회수 메서드
    //비디오를 시청하다 광고시점이 오면 client가 server에 요청
    public void adviewcount(AdviewcountRequestDto adviewcountRequestDto) {
        System.out.println("adviewcount 메서드 실행 중 - videoId: " + adviewcountRequestDto.getVideoId());
//        Videos video = videoRepository.findById(adviewcountRequestDto.getVideoId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오입니다."));
        LocalDate today = LocalDate.now();

        // DailyVideoView에서 오늘 날짜와 해당 비디오 ID로 된 기록이 있는지 확인
        DailyVideoView dailyVideoView = dailyVideoViewRepository.findByVideoIdAndDate(adviewcountRequestDto.getVideoId(), today)
                .orElseGet(() -> new DailyVideoView(adviewcountRequestDto.getVideoId(), today)); // 없으면 새로운 레코드 생성

        // 광고 시청 횟수 증가
        dailyVideoView.incrementAdViewCount();

        // 변경 사항 저장
        dailyVideoViewRepository.save(dailyVideoView);

    }


    // IP 주소를 가져오기 위한 메서드
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");  // 프록시를 통해 요청이 올 경우
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();  // 마지막으로 직접 클라이언트의 IP를 가져옴
        }
        return ipAddress;
    }

}

