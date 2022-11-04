package com.meetup.backend.service.meeting;

import com.meetup.backend.dto.schedule.AllScheduleResponseDto;
import com.meetup.backend.dto.schedule.meeting.MeetingRequestDto;
import com.meetup.backend.dto.schedule.meeting.MeetingResponseDto;
import com.meetup.backend.dto.schedule.meeting.MeetingUpdateRequestDto;
import com.meetup.backend.entity.channel.Channel;
import com.meetup.backend.entity.meetup.Meetup;
import com.meetup.backend.entity.schedule.Meeting;
import com.meetup.backend.entity.schedule.Schedule;
import com.meetup.backend.entity.user.User;
import com.meetup.backend.exception.ApiException;
import com.meetup.backend.exception.ExceptionEnum;
import com.meetup.backend.repository.channel.ChannelRepository;
import com.meetup.backend.repository.channel.ChannelUserRepository;
import com.meetup.backend.repository.schedule.MeetingRepository;
import com.meetup.backend.repository.meetup.MeetupRepository;
import com.meetup.backend.repository.schedule.ScheduleRepository;
import com.meetup.backend.repository.user.UserRepository;
import com.meetup.backend.service.Client;
import com.meetup.backend.service.auth.AuthService;
import com.meetup.backend.util.converter.StringToLocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.meetup.backend.exception.ExceptionEnum.*;

/**
 * created by myeongseok on 2022/10/30
 * updated by myeongseok on 2022/11/04
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MeetingServiceImpl implements MeetingService {

    private final ScheduleRepository scheduleRepository;

    private final ChannelRepository channelRepository;

    private final ChannelUserRepository channelUserRepository;

    private final MeetingRepository meetingRepository;

    private final MeetupRepository meetupRepository;

    private final UserRepository userRepository;

    private final AuthService authService;

    // 미팅 상세정보 반환
    @Override
    public MeetingResponseDto getMeetingResponseDtoById(String userId, Long meetingId) {
        // 로그인 유저
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new ApiException(MEETING_NOT_FOUND));
        Meetup meetup = meeting.getMeetup(); // 해당 미팅의 밋업
        Channel channel = meetup.getChannel(); // 해당 밋업의 채널
        // 현재 로그인 유저가 채널에 속해있지 않거나, 미팅 관리자가 아닌경우 접근 불가
        if (!channelUserRepository.existsByChannelAndUser(channel, user) || !meeting.getMeetup().getManager().equals(user)) {
            throw new ApiException(ACCESS_DENIED);
        }
        return MeetingResponseDto.of(meeting, meetup, user, meetup.getManager());
    }

    //미팅 정보 등록
    @Override
    @Transactional
    public Long createMeeting(String userId, MeetingRequestDto meetingRequestDto) {
        if (meetingRequestDto.getStart().length() != "yyyy-MM-dd HH:mm:ss".length() || meetingRequestDto.getEnd().length() != "yyyy-MM-dd HH:mm:ss".length()) {
            throw new ApiException(DATE_FORMAT_EX);
        }
        User loginUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        LocalDateTime start = StringToLocalDateTime.strToLDT(meetingRequestDto.getStart());
        LocalDateTime end = StringToLocalDateTime.strToLDT(meetingRequestDto.getEnd());
        String title = meetingRequestDto.getTitle();
        String content = meetingRequestDto.getContent();
        Meetup meetup = meetupRepository.findById(meetingRequestDto.getMeetupId()).orElseThrow(() -> new ApiException(MEETUP_NOT_FOUND));
        Channel channel = channelRepository.findById(meetup.getChannel().getId()).orElseThrow(() -> new ApiException(CHANNEL_NOT_FOUND));

        if (!channelUserRepository.existsByChannelAndUser(channel, loginUser))
            throw new ApiException(ACCESS_DENIED);

        Meeting meeting = Meeting.builder().title(title).content(content).start(start).end(end).meetup(meetup).user(loginUser).build();
        MattermostClient client = Client.getClient();
        client.setAccessToken(authService.getMMSessionToken(userId));
        String startTime = meetingRequestDto.getStart().substring(5, 16);
        String endTime = meetingRequestDto.getEnd().substring(11, 16);
        String message = "### " + meetingRequestDto.getTitle() + " \n ###### :bookmark: " + meetingRequestDto.getContent() + " \n ###### :date: " + startTime + " ~ " + endTime + "\n------";
        client.createPost(new Post(channel.getId(), message));
        return meetingRepository.save(meeting).getId();
    }

    // 미팅 정보 수정
    @Override
    public Long updateMeeting(String userId, MeetingUpdateRequestDto meetingUpdateRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Meeting meeting = meetingRepository.findById(meetingUpdateRequestDto.getId()).orElseThrow(() -> new ApiException(MEETING_NOT_FOUND));
        User managerUser = userRepository.findById(meeting.getMeetup().getManager().getId()).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        if (!meeting.getMeetup().getManager().equals(user) || !meeting.getUser().equals(user)) {
            throw new ApiException(ACCESS_DENIED);
        }
        LocalDateTime start = StringToLocalDateTime.strToLDT(meetingUpdateRequestDto.getStart());
        LocalDateTime end = StringToLocalDateTime.strToLDT(meetingUpdateRequestDto.getEnd());
        String date = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        AllScheduleResponseDto userAllScheduleResponseDto = getSchedule(userId, userId, date, 1);
        AllScheduleResponseDto managerAllScheduleResponseDto = getSchedule(userId, userId, date, 1);
        if (!userAllScheduleResponseDto.isPossibleRegiser(start, end) || managerAllScheduleResponseDto.isPossibleRegiser(start, end))
            throw new ApiException(ExceptionEnum.DUPLICATE_UPDATE_DATETIME);
        meeting.update(meetingUpdateRequestDto);
        return meeting.getId();
    }

    // 미팅 정보 삭제
    @Override
    @Transactional
    public void deleteMeeting(String userId, Long meetingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new ApiException(MEETING_NOT_FOUND));
        // 로그인 유저가 미팅 관리자가 아니거나 신청자가 아니라면 삭제 불가
        if (!meeting.getMeetup().getManager().equals(user) || !meeting.getUser().equals(user)) {
            throw new ApiException(ACCESS_DENIED);
        }
        meetingRepository.delete(meeting);
    }

    public AllScheduleResponseDto getSchedule(String loginUserId, String targetUserId, String date, int p) {
        User loginUser = userRepository.findById(loginUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        List<Meetup> meetups = meetupRepository.findByManager(targetUser);

        boolean flag = false;
        if (loginUserId.equals(targetUserId)) {
            flag = true;
        }
        for (Meetup meetup : meetups) {
            if (channelUserRepository.existsByChannelAndUser(meetup.getChannel(), loginUser)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ApiException(ACCESS_DENIED_THIS_SCHEDULE);
        }
        LocalDateTime from = StringToLocalDateTime.strToLDT(date);
        LocalDateTime to = from.plusDays(p);
        List<Schedule> schedules = scheduleRepository.findAllByStartBetweenAndUser(from, to, targetUser);

        // 해당 스케줄 주인의 밋업 리스트
        List<Meetup> meetupList = meetupRepository.findByManager(targetUser);
        List<Meeting> meetingToMe = new ArrayList<>();
        if (meetupList.size() > 0) {
            for (Meetup mu : meetupList) {
                // 스케줄 주인이 신청 받은 미팅(컨,프,코,교 시점)
                meetingToMe.addAll(meetingRepository.findByMeetup(mu));
            }
        }
        return AllScheduleResponseDto.of(schedules, meetingToMe);
    }


}
