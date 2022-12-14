package com.meetup.backend.docs.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetup.backend.controller.ScheduleController;
import com.meetup.backend.dto.schedule.AllScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleRequestDto;
import com.meetup.backend.dto.schedule.ScheduleResponseDto;
import com.meetup.backend.dto.schedule.ScheduleUpdateRequestDto;
import com.meetup.backend.dto.user.UserInfoDto;
import com.meetup.backend.entity.channel.Channel;
import com.meetup.backend.entity.channel.ChannelType;
import com.meetup.backend.entity.meetup.Meetup;
import com.meetup.backend.entity.party.Party;
import com.meetup.backend.entity.schedule.Meeting;
import com.meetup.backend.entity.schedule.Schedule;
import com.meetup.backend.entity.schedule.ScheduleType;
import com.meetup.backend.entity.team.Team;
import com.meetup.backend.entity.team.TeamType;
import com.meetup.backend.entity.user.RoleType;
import com.meetup.backend.entity.user.User;
import com.meetup.backend.service.auth.AuthService;
import com.meetup.backend.service.meeting.ScheduleService;
import com.meetup.backend.util.converter.LocalDateUtil;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.meetup.backend.entity.user.RoleType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class ScheduleControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private AuthService authService;

    @Test
    public void getSchedule() throws Exception {
        // given
        UserInfoDto userInfoDto = new UserInfoDto("userId", "hong");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        ScheduleResponseDto scheduleResponseDto = ScheduleResponseDto.builder()
                .id(1L)
                .open(true)
                .partyId(2L)
                .partyName("2???")
                .title("??? ?????????")
                .content("?????? ??????")
                .start(LocalDateUtil.strToLDT("2022-11-16 12:00:00"))
                .end(LocalDateUtil.strToLDT("2022-11-16 13:00:00"))
                .meetupId(3L)
                .managerId("managerId")
                .managerName("manager")
                .myWebex("www.myWebex.com")
                .diffWebex("www.diffWebex.com")
                .type(ScheduleType.Schedule)
                .userId("userId")
                .userName("hong")
                .build();

        given(scheduleService.getScheduleDetail(anyString(), anyLong()))
                .willReturn(scheduleResponseDto);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/schedule/{scheduleId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("schedule(meeting)-detail-by-scheduleId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("partyId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("partyName").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("start").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("end").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("meetupId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("managerId").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("managerName").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("myWebex").type(JsonFieldType.STRING).description("????????? ?????? ?????? ?????????"),
                                fieldWithPath("diffWebex").type(JsonFieldType.STRING).description("????????? ?????? ?????? ?????????"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("????????? ?????? (????????? or ??????)"),
                                fieldWithPath("userId").type(JsonFieldType.STRING).description("?????????(??????) ????????? ?????? ?????????"),
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("?????????(??????) ????????? ?????? ??????"),
                                fieldWithPath("delete").type(JsonFieldType.BOOLEAN).description("?????? ??????")
                        )
                ));
    }

    @Test
    public void createSchedule() throws Exception {
        // given
        UserInfoDto userInfoDto = new UserInfoDto("userId", "hong");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        ScheduleRequestDto scheduleRequestDto = ScheduleRequestDto.builder()
                .title("????????? ??????")
                .content("?????? ??????")
                .start("2022-11-18 12:00:00")
                .end("2022-11-18 13:00:00")
                .open(true)
                .build();

        Long scheduleId = 1L;
        given(scheduleService.createSchedule(anyString(), any(ScheduleRequestDto.class))).willReturn(scheduleId);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/schedule")
                .content(objectMapper.writeValueAsString(scheduleRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("schedule-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("start").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("end").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????")
                        ),
                        responseBody()
                ));
    }

    @Test
    public void updateSchedule() throws Exception {
        // given
        UserInfoDto userInfoDto = new UserInfoDto("userId", "hong");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        ScheduleUpdateRequestDto scheduleRequestDto = ScheduleUpdateRequestDto.builder()
                .id(1L)
                .title("????????? ??????")
                .content("?????? ??????")
                .start("2022-11-18 12:00:00")
                .end("2022-11-18 13:00:00")
                .open(true)
                .build();

        Long scheduleId = 1L;
        given(scheduleService.updateSchedule(anyString(), any(ScheduleUpdateRequestDto.class))).willReturn(scheduleId);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/schedule")
                .content(objectMapper.writeValueAsString(scheduleRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("schedule-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("start").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("end").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????")
                        ),
                        responseBody()
                ));
    }

    @Test
    public void deleteSchedule() throws Exception {
        // given
        UserInfoDto userInfoDto = new UserInfoDto("userId", "hong");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        Long requestDto = 1L;
        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/schedule/{scheduleId}", requestDto))
                .andExpect(status().isOk())
                .andDo(document("schedule-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseBody()
                ));
    }

    @Test
    public void getScheduleByMeetupAndDate() throws Exception {
        // given
        UserInfoDto userInfoDto = new UserInfoDto("userId", "hong");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<Schedule> scheduleList = new ArrayList<>();
        List<Meeting> meetingList = new ArrayList<>();
        List<Meeting> partyMeetings = new ArrayList<>();

        scheduleList.add(new Schedule(
                LocalDateUtil.strToLDT("2022-11-15 13:00:00")
                , LocalDateUtil.strToLDT("2022-11-15 14:00:00")
                , "?????? ??????"
                , "??????"
                , true
                , new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true)));

        scheduleList.add(new Schedule(
                LocalDateUtil.strToLDT("2022-11-16 15:00:00")
                , LocalDateUtil.strToLDT("2022-11-16 16:00:00")
                , "?????? ?????????"
                , "?????? ??????????????????."
                , false
                , new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true)));

        scheduleList.add(Meeting.builder()
                .start(LocalDateUtil.strToLDT("2022-11-15 11:00:00"))
                .end(LocalDateUtil.strToLDT("2022-11-15 12:00:00"))
                .title("?????? ?????? ??????")
                .content("?????? ?????? ???????????????.")
                .open(false)
                .user(new User("userId", "password", "hong", "www.webex2.com", ROLE_Coach, true))
                .meetup(new Meetup("??????", "#FFFFF", new User("pro", "password", "pro", "www.webex.com", ROLE_Coach, true), new Channel("channel1", "????????????", "????????????", ChannelType.Private, new Team("team1", "?????????", "?????????", TeamType.Invite))))
                .build());

        Meeting meeting1 = Meeting.builder()
                .start(LocalDateUtil.strToLDT("2022-11-16 15:00:00"))
                .end(LocalDateUtil.strToLDT("2022-11-16 16:00:00"))
                .title("?????? ?????? ??????")
                .content("?????? ?????? ???????????????.")
                .open(false)
                .user(new User("user2", "password", "user2", "www.webex2.com", ROLE_Student, true))
                .meetup(new Meetup("2???", "#FFFFF", new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true), new Channel("channel1", "????????????", "????????????", ChannelType.Private, new Team("team1", "?????????", "?????????", TeamType.Invite))))
                .build();
        Meeting meeting2 = Meeting.builder()
                .start(LocalDateUtil.strToLDT("2022-11-16 17:00:00"))
                .end(LocalDateUtil.strToLDT("2022-11-16 18:00:00"))
                .title("?????? ??????")
                .content("?????? ?????? ???????????????.")
                .open(false)
                .user(new User("user3", "password", "user3", "www.webex3.com", ROLE_Student, true))
                .meetup(new Meetup("3???", "#FFFFF", new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true), new Channel("channel1", "????????????", "????????????", ChannelType.Private, new Team("team1", "?????????", "?????????", TeamType.Invite))))
                .build();
        meetingList.add(meeting1);
        meetingList.add(meeting2);

        partyMeetings.add(new Meeting(
                LocalDateUtil.strToLDT("2022-11-17 15:00:00")
                , LocalDateUtil.strToLDT("2022-11-17 16:00:00")
                , "2??? ?????? ??????"
                , "?????? ?????????"
                , true
                , new User("user2", "password", "user2", "www.webex2.com", ROLE_Student, true)
                , new Meetup("2???", "#FFFFF", new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true)
                , new Channel("channel1", "????????????", "????????????", ChannelType.Private, new Team("team1", "?????????", "?????????", TeamType.Invite)))
                , new Party("2???")
        ));

        partyMeetings.add(new Meeting(
                LocalDateUtil.strToLDT("2022-11-18 15:00:00")
                , LocalDateUtil.strToLDT("2022-11-18 16:00:00")
                , "3??? ?????? ??????"
                , "?????? ?????????"
                , true
                , new User("user7", "password", "user7", "www.webex7.com", ROLE_Student, true)
                , new Meetup("7???", "#FFFFF", new User("userId", "password", "hong", "www.webex.com", ROLE_Coach, true)
                , new Channel("channel1", "????????????", "????????????", ChannelType.Private, new Team("team1", "?????????", "?????????", TeamType.Invite)))
                , new Party("7???")
        ));


        AllScheduleResponseDto responseDto = AllScheduleResponseDto.of(scheduleList, meetingList, partyMeetings, userInfoDto.getId());
        String targetId = "coach1";
        String date = "2022-11-13 00:00:00";
        given(scheduleService.getScheduleByUserAndDate(userInfoDto.getId(), targetId, date))
                .willReturn(responseDto);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/schedule")
                .param("targetId", targetId)
                .param("date", date))
                .andExpect(status().isOk())
                .andDo(document("target-user-schedule-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("meetingFromMe[]").type(JsonFieldType.ARRAY).description("????????? ??????"),
                                fieldWithPath("meetingFromMe[].id").description("????????? ?????? ?????????"),
                                fieldWithPath("meetingFromMe[].open").type(JsonFieldType.BOOLEAN).description("????????? ?????? ????????????"),
                                fieldWithPath("meetingFromMe[].start").type(JsonFieldType.STRING).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("meetingFromMe[].end").type(JsonFieldType.STRING).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("meetingFromMe[].title").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("meetingFromMe[].content").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("meetingFromMe[].userId").type(JsonFieldType.STRING).description("????????? ????????? ???????????? ?????????(???????????? or ?????????)"),
                                fieldWithPath("meetingFromMe[].userName").type(JsonFieldType.STRING).description("????????? ????????? ???????????? ??????(???????????? or ?????????)"),
                                fieldWithPath("meetingFromMe[].meetupName").type(JsonFieldType.STRING).description("????????? ????????? ?????? ??????"),
                                fieldWithPath("meetingFromMe[].meetupColor").type(JsonFieldType.STRING).description("????????? ????????? ?????? ???"),

                                fieldWithPath("meetingToMe[]").type(JsonFieldType.ARRAY).description("???????????? ??????"),
                                fieldWithPath("meetingToMe[].id").description("???????????? ?????? ?????????"),
                                fieldWithPath("meetingToMe[].open").type(JsonFieldType.BOOLEAN).description("???????????? ?????? ????????????"),
                                fieldWithPath("meetingToMe[].start").type(JsonFieldType.STRING).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("meetingToMe[].end").type(JsonFieldType.STRING).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("meetingToMe[].title").type(JsonFieldType.STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("meetingToMe[].content").type(JsonFieldType.STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("meetingToMe[].userId").type(JsonFieldType.STRING).description("????????? ????????? ???????????? ?????????"),
                                fieldWithPath("meetingToMe[].userName").type(JsonFieldType.STRING).description("????????? ????????? ???????????? ??????"),
                                fieldWithPath("meetingToMe[].meetupName").type(JsonFieldType.STRING).description("???????????? ????????? ?????? ??????"),
                                fieldWithPath("meetingToMe[].meetupColor").type(JsonFieldType.STRING).description("???????????? ????????? ?????? ???"),

                                fieldWithPath("scheduleResponseList[]").type(JsonFieldType.ARRAY).description("?????????"),
                                fieldWithPath("scheduleResponseList[].id").description("????????? ?????????"),
                                fieldWithPath("scheduleResponseList[].open").type(JsonFieldType.BOOLEAN).description("????????? ????????????"),
                                fieldWithPath("scheduleResponseList[].start").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("scheduleResponseList[].end").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("scheduleResponseList[].title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("scheduleResponseList[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("scheduleResponseList[].userId").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("scheduleResponseList[].userName").type(JsonFieldType.STRING).description("????????? ?????? ??????"),

                                fieldWithPath("partyMeetingResponseList[]").type(JsonFieldType.ARRAY).description("?????? ??????"),
                                fieldWithPath("partyMeetingResponseList[].id").description("?????? ?????? ?????????"),
                                fieldWithPath("partyMeetingResponseList[].open").type(JsonFieldType.BOOLEAN).description("?????? ?????? ????????????"),
                                fieldWithPath("partyMeetingResponseList[].start").type(JsonFieldType.STRING).description("?????? ?????? ????????????"),
                                fieldWithPath("partyMeetingResponseList[].end").type(JsonFieldType.STRING).description("?????? ?????? ????????????"),
                                fieldWithPath("partyMeetingResponseList[].title").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("partyMeetingResponseList[].content").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("partyMeetingResponseList[].userId").type(JsonFieldType.STRING).description("?????? ?????? ????????? ?????????"),
                                fieldWithPath("partyMeetingResponseList[].userName").type(JsonFieldType.STRING).description("?????? ?????? ????????? ??????"),
                                fieldWithPath("partyMeetingResponseList[].meetupName").type(JsonFieldType.STRING).description("?????? ????????? ?????? ??????"),
                                fieldWithPath("partyMeetingResponseList[].meetupColor").type(JsonFieldType.STRING).description("?????? ????????? ?????? ???"),
                                fieldWithPath("partyMeetingResponseList[].partyId").description("?????? ????????? ?????? ?????????"),
                                fieldWithPath("partyMeetingResponseList[].partyName").type(JsonFieldType.STRING).description("?????? ????????? ?????? ??????")
                        )
                ));
    }
}
