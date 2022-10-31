package com.meetup.backend.service.meeting;

import com.meetup.backend.dto.schedule.AllScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleRequestDto;
import com.meetup.backend.dto.schedule.ScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleUpdateRequestDto;

import java.util.List;

/**
 * created by seongmin on 2022/10/25
 */
public interface ScheduleService {

    ScheduleResponseDto getScheduleResponseDtoById(String userId, Long scheduleId);

    List<ScheduleResponseDto> getScheduleResponseDtoByUserAndDate(String loginUserId, String date);

    AllScheduleResponseDto getScheduleResponseDtoByUserAndDate(String loginUserId, Long meetupId, String date);

    Long createSchedule(String userId, ScheduleRequestDto scheduleRequestDto);

    Long updateSchedule(String userId, ScheduleUpdateRequestDto scheduleUpdateRequestDto);

    void deleteSchedule(String userId, Long scheduleId);
}
