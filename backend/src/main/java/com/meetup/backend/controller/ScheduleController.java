package com.meetup.backend.controller;

import com.meetup.backend.dto.schedule.ScheduleRequestDto;
import com.meetup.backend.dto.schedule.ScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleUpdateRequestDto;
import com.meetup.backend.service.auth.AuthService;
import com.meetup.backend.service.meeting.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * created by myeongseok on 2022/10/23
 * updated by seongmin on 2022/10/31
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    private final AuthService authService;

    // 스케쥴id로 단일 스케쥴 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getSchedule(@PathVariable("scheduleId") Long scheduleId) {
        log.info("scheduleId = {}", scheduleId);
        String userId = authService.getMyInfoSecret().getId();
        ScheduleResponseDto scheduleResponseDto = scheduleService.getScheduleResponseDtoById(userId, scheduleId);
        return ResponseEntity.status(OK).body(scheduleResponseDto);

    }

    // 로그인 유저의 일정 가져오기
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getScheduleResponseDtoByUserAndDate(@PathVariable("date") String date) {
        log.info("date = {}", date);
        String userId = authService.getMyInfoSecret().getId();
        List<ScheduleResponseDto> scheduleResponseDto = scheduleService.getScheduleResponseDtoByUserAndDate(userId, date);
        return ResponseEntity.status(OK).body(scheduleResponseDto);

    }


    // 스케쥴 등록
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleRequestDto scheduleRequestDto) {
        log.info("scheduleRequestDto = {}", scheduleRequestDto);
        String userId = authService.getMyInfoSecret().getId();
        Long scheduleId = scheduleService.createSchedule(userId, scheduleRequestDto);
        return ResponseEntity.status(CREATED).body(scheduleId);
    }

    // 스케쥴 id 해당되는 스케쥴 수정
    @PatchMapping
    public ResponseEntity<?> updateSchedule(@RequestBody @Valid ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        log.info("scheduleUpdateRequestDto : {}", scheduleUpdateRequestDto);
        String userId = authService.getMyInfoSecret().getId();
        Long scheduleId = scheduleService.updateSchedule(userId, scheduleUpdateRequestDto);

        return ResponseEntity.status(CREATED).body(scheduleId);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("scheduleId") Long scheduleId) {
        log.info("scheduleId :{}", scheduleId);
        String userId = authService.getMyInfoSecret().getId();
        scheduleService.deleteSchedule(userId, scheduleId);

        return ResponseEntity.status(OK).build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getScheduleByMeAndDate(@RequestParam @Valid String date) {
        AllScheduleResponseDto result = scheduleService.getScheduleResponseDtoByUserAndDate(authService.getMyInfoSecret().getId(), date);
        return ResponseEntity.status(OK).body(result);
    }

}