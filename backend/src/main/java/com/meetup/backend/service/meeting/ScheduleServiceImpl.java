package com.meetup.backend.service.meeting;

import com.meetup.backend.dto.schedule.AllScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleRequestDto;
import com.meetup.backend.dto.schedule.ScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleUpdateRequestDto;
import com.meetup.backend.entity.meetup.Meetup;
import com.meetup.backend.entity.party.Party;
import com.meetup.backend.entity.party.PartyUser;
import com.meetup.backend.entity.schedule.Meeting;
import com.meetup.backend.entity.schedule.Schedule;
import com.meetup.backend.entity.schedule.ScheduleType;
import com.meetup.backend.entity.user.User;
import com.meetup.backend.exception.ApiException;
import com.meetup.backend.exception.ExceptionEnum;
import com.meetup.backend.repository.channel.ChannelUserRepository;
import com.meetup.backend.repository.meetup.MeetupRepository;
import com.meetup.backend.repository.party.PartyUserRepository;
import com.meetup.backend.repository.schedule.MeetingRepository;
import com.meetup.backend.repository.schedule.ScheduleRepository;
import com.meetup.backend.repository.user.UserRepository;
import com.meetup.backend.util.converter.LocalDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.meetup.backend.exception.ExceptionEnum.*;

/**
 * created by myeongseok on 2022/10/30
 * updated by seongmin on 2022/11/14
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ChannelUserRepository channelUserRepository;
    private final MeetupRepository meetupRepository;
    private final MeetingRepository meetingRepository;
    private final PartyUserRepository partyUserRepository;


    // ???????????? ID??? ?????? ?????? ?????? (?????????)
    @Override
    public ScheduleResponseDto getScheduleDetail(String userId, Long scheduleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ApiException(SCHEDULE_NOT_FOUND));
        if (schedule.getType().equals(ScheduleType.Schedule)) {
            if (!schedule.isOpen() && schedule.getUser() != user) {
                throw new ApiException(ACCESS_DENIED_THIS_SCHEDULE);
            }
        }
        if (schedule.getType().equals(ScheduleType.Meeting)) {
            // ?????? ????????? ????????? ????????? ?????????, ?????? ????????? ???????????? ????????? ?????????
            boolean chkAccess = true;
            Meeting meeting = (Meeting) schedule;
            if (!meeting.isOpen() && meeting.getUser() != user && meeting.getMeetup().getManager() != user) {
                chkAccess = false;
            }
            // ?????? ?????? : ?????? ????????? ?????? ??????????
            boolean chkGroupIn = false;
            if (meeting.getParty() != null) {
                // ?????? ????????? ????????? ????????? ????????? ????????????????
                List<PartyUser> partyUsers = meeting.getParty().getPartyUsers();
                for (PartyUser partyUser : partyUsers) {
                    if (partyUser.getUser().getId().equals(userId)) {
                        chkGroupIn = true;
                        break;
                    }
                }
            }

            if (!chkAccess && !chkGroupIn) {
                throw new ApiException(ACCESS_DENIED_THIS_SCHEDULE);
            }
        }
        // ????????? ????????? ?????? ?????? ????????? ??? ??????
        // ???????????? open false
        // & ??? ???????????? ??????

        // ?????? ????????? ?????? ?????? ????????? ??? ??????
        // ????????? open false
        // & ?????? ????????? ????????? ??????
        // & ?????? ???????????? ????????? ??????
        return ScheduleResponseDto.of(schedule, user);
    }

    // ?????? user, ????????? ?????? id, date??? ?????? ????????????
    @Override
    public AllScheduleResponseDto getScheduleByUserAndDate(String loginUserId, String targetUserId, String date) {
        return getSchedule(loginUserId, targetUserId, date, 7, getScheduleListWithoutLock(date, loginUserId, targetUserId, 7));
    }

    // ????????? ?????? ??????
    @Override
    @Transactional(isolation = Isolation.DEFAULT)
    public Long createSchedule(String userId, ScheduleRequestDto scheduleRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        LocalDateTime start = LocalDateUtil.strToLDT(scheduleRequestDto.getStart());
        LocalDateTime end = LocalDateUtil.strToLDT(scheduleRequestDto.getEnd());

        diffDurationCheck(start, end);

        // ?????? ?????? ??????
        String date = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        AllScheduleResponseDto allScheduleResponseDto = getSchedule(userId, userId, date, 1, getScheduleListWithLock(date, userId, userId, 1));
        if (!allScheduleResponseDto.isPossibleRegister(start, end))
            throw new ApiException(ExceptionEnum.DUPLICATE_INSERT_DATETIME);

        String title = scheduleRequestDto.getTitle();
        String content = scheduleRequestDto.getContent();
        Schedule schedule = new Schedule(start, end, title, content, scheduleRequestDto.isOpen(), user);

        return scheduleRepository.save(schedule).getId();
    }


    // ????????? ?????? ??????
    @Override
    @Transactional
    public Long updateSchedule(String userId, ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleUpdateRequestDto.getId()).orElseThrow(() -> new ApiException(SCHEDULE_NOT_FOUND));
        if (!user.getId().equals(schedule.getUser().getId())) {
            throw new ApiException(ACCESS_DENIED);
        }
        LocalDateTime start = LocalDateUtil.strToLDT(scheduleUpdateRequestDto.getStart());
        LocalDateTime end = LocalDateUtil.strToLDT(scheduleUpdateRequestDto.getEnd());
        // ?????? ????????? ?????? ????????? ?????? ?????? (30??? ????????? ??????)
        diffDurationCheck(start, end);
        // ?????? ?????? ??????
        String date = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        AllScheduleResponseDto allScheduleResponseDto = getSchedule(userId, userId, date, 1, getScheduleListWithLock(date, userId, userId, 1));
        if (!allScheduleResponseDto.isPossibleRegister(start, end, scheduleUpdateRequestDto.getId()))
            throw new ApiException(ExceptionEnum.DUPLICATE_UPDATE_DATETIME);

        schedule.update(scheduleUpdateRequestDto);
        return schedule.getId();
    }

    // ????????? ?????? ??????
    @Override
    @Transactional
    public void deleteSchedule(String userId, Long scheduleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ApiException(SCHEDULE_NOT_FOUND));
        if (!user.getId().equals(schedule.getUser().getId())) {
            throw new ApiException(ACCESS_DENIED);
        }
        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional
    public List<Schedule> getScheduleListWithLock(String date, String loginUserId, String targetUserId, int p) {
        User loginUser = userRepository.findById(loginUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        accessCheck(loginUserId, targetUserId, loginUser, targetUser);

        LocalDateTime from = LocalDateUtil.strToLDT(date);
        LocalDateTime to = from.plusDays(p);
        if (p == 1) {
            from = from.minusDays(p);
        }
        return scheduleRepository.findAllByStartBetweenAndUser(from, to, targetUser);
    }

    @Override
    public List<Schedule> getScheduleListWithoutLock(String date, String loginUserId, String targetUserId, int p) {
        User loginUser = userRepository.findById(loginUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        accessCheck(loginUserId, targetUserId, loginUser, targetUser);

        LocalDateTime from = LocalDateUtil.strToLDT(date);
        LocalDateTime to = from.plusDays(p);
        if (p == 1) {
            from = from.minusDays(p);
        }
        return scheduleRepository.findByUser(from, to, targetUser);
    }

    @Override
    public AllScheduleResponseDto getSchedule(String loginUserId, String targetUserId, String date, int p, List<Schedule> schedules) {
        User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        LocalDateTime from = LocalDateUtil.strToLDT(date);
        LocalDateTime to = from.plusDays(p);
        // ?????? ????????? ????????? ?????? ?????????
        List<Meetup> meetupList = meetupRepository.findByManager(targetUser);
        List<Meeting> meetingToMe = new ArrayList<>();
        if (meetupList.size() > 0) {
            for (Meetup meetup : meetupList) {
                // ????????? ????????? ?????? ?????? ??????(???,???,???,??? ??????)
                meetingToMe.addAll(meetingRepository.findByMeetUp(from, to, meetup));
            }
        }
        // ?????? ????????? ????????? ?????? ?????? ????????? ?????????
        List<PartyUser> partyUserList = partyUserRepository.findByUser(targetUser);
        List<Party> partyList = partyUserList.stream().map(PartyUser::getParty).collect(Collectors.toList());
        List<Meeting> partyMeetingList = new ArrayList<>();
        for (Party party : partyList) {
            List<Meeting> allByStartBetweenAndParty = meetingRepository.findAllByStartBetweenAndParty(from, to, party);
            partyMeetingList.addAll(allByStartBetweenAndParty);
        }

        return AllScheduleResponseDto.of(schedules, meetingToMe, partyMeetingList, loginUserId);
    }

    @Override
    public void diffDurationCheck(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        if (duration.getSeconds() < 1800)
            throw new ApiException(TOO_SHORT_DURATION);
    }

    private void accessCheck(String loginUserId, String targetUserId, User loginUser, User targetUser) {
        List<Meetup> meetups = meetupRepository.findByManager(targetUser);

        boolean flag = false;
        if (loginUserId.equals(targetUserId)) {
            flag = true;
        }

        // ????????? ?????????, ????????? ????????? ?????? ????????? ?????? ?????? ??? ??? ??????
        for (Meetup meetup : meetups) {
            if (channelUserRepository.existsByChannelAndUser(meetup.getChannel(), loginUser)) {
                flag = true;
                break;
            }
        }

        List<Meetup> meetupsByLoginUser = meetupRepository.findByManager(loginUser);
        for (Meetup meetup : meetupsByLoginUser) {
            if (channelUserRepository.existsByChannelAndUser(meetup.getChannel(), targetUser)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new ApiException(ACCESS_DENIED_THIS_SCHEDULE);
        }
    }
}
