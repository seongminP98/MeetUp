package com.meetup.backend.docs.meetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetup.backend.controller.MeetUpController;
import com.meetup.backend.dto.channel.ChannelCreateRequestDto;
import com.meetup.backend.dto.channel.ChannelResponseDto;
import com.meetup.backend.dto.meetup.*;
import com.meetup.backend.dto.team.TeamActivateRequestDto;
import com.meetup.backend.dto.team.TeamActivateResponseDto;
import com.meetup.backend.dto.team.TeamResponseDto;
import com.meetup.backend.dto.user.UserInfoDto;
import com.meetup.backend.entity.channel.Channel;
import com.meetup.backend.entity.channel.ChannelType;
import com.meetup.backend.entity.channel.ChannelUser;
import com.meetup.backend.entity.team.Team;
import com.meetup.backend.entity.team.TeamType;
import com.meetup.backend.service.auth.AuthService;
import com.meetup.backend.service.channel.ChannelService;
import com.meetup.backend.service.channel.ChannelUserService;
import com.meetup.backend.service.meetup.MeetupService;
import com.meetup.backend.service.team.TeamUserService;
import com.meetup.backend.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetUpController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class MeetupControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private TeamUserService teamUserService;

    @MockBean
    private ChannelService channelService;

    @MockBean
    private ChannelUserService channelUserService;

    @MockBean
    private MeetupService meetupService;

    @MockBean
    private AuthService authService;

    @Test
    public void getTeamByUserId() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "nickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();
        teamResponseDtoList.add(TeamResponseDto.builder()
                .id("teamId")
                .displayName("teamDisplayName")
                .type(TeamType.Open)
                .build());
        given(teamUserService.getTeamByUser(anyString())).willReturn(teamResponseDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/team"))
                .andExpect(status().isOk())
                .andDo(document("team-activate-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("?????? ?????? ID"),
                                fieldWithPath("[].displayName").description("??????????????? ???????????? ?????? ??????"),
                                fieldWithPath("[].type").description("?????? ?????? (Open, Invite)")
                        )
                ));
    }

    @Test
    public void getActivatedChannel() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "nickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<ChannelResponseDto> channelResponseDtoList = new ArrayList<>();
        channelResponseDtoList.add(ChannelResponseDto.builder()
                .id("channelId")
                .displayName("channelDisplayName")
                .TeamId("teamId")
                .build());
        given(channelUserService.getActivatedChannelByUser(anyString())).willReturn(channelResponseDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/channel"))
                .andExpect(status().isOk())
                .andDo(document("channel-activate-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("????????? ?????? ID"),
                                fieldWithPath("[].displayName").description("??????????????? ???????????? ????????? ??????"),
                                fieldWithPath("[].teamId").description("????????? ?????? ?????? ?????? ID")
                        )
                ));
    }

    @Test
    public void registerMeetup() throws Exception {
        MeetupRequestDto meetupRequestDto = MeetupRequestDto.builder()
                .title("meetupTitle")
                .color("meetupColor")
                .channelId("meetupChannelId")
                .build();

        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/meetup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetupRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("meetup-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("????????? ??????"),
                                fieldWithPath("color").description("????????? ??????"),
                                fieldWithPath("channelId").description("????????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void getMeetupInfoById() throws Exception {
        MeetupUpdateResponseDto responseDto = MeetupUpdateResponseDto.builder()
                .id(11L)
                .title("meetupTitle")
                .color("meetupColor")
                .teamName("meetupTeamName")
                .channelName("meetupChannelName")
                .build();
        given(meetupService.getMeetupInfo(anyLong())).willReturn(responseDto);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/11"))
                .andExpect(status().isOk())
                .andDo(document("meetup-detail-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("????????? ?????? Id"),
                                fieldWithPath("title").description("????????? ??????"),
                                fieldWithPath("color").description("????????? ??????"),
                                fieldWithPath("teamName").description("????????? ?????? ?????? ??????"),
                                fieldWithPath("channelName").description("????????? ?????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void updateMeetup() throws Exception {
        MeetupUpdateRequestDto meetupUpdateRequestDto = MeetupUpdateRequestDto.builder()
                .title("meetup")
                .color("color")
                .build();

        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/meetup/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetupUpdateRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("meetup-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("????????? ????????? ??????"),
                                fieldWithPath("color").description("????????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void deleteMeetup() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/meetup/11"))
                .andExpect(status().isOk())
                .andDo(document("meetup-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    public void getCalendarList() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<ChannelUser> channelUserList = new ArrayList<>();
        given(channelUserService.getChannelUserByUser(anyString())).willReturn(channelUserList);

        List<CalendarResponseDto> calendarResponseDtoList = new ArrayList<>();
        calendarResponseDtoList.add(CalendarResponseDto.builder()
                .id("mangerId")
                .userName("managerName")
                .build());
        given(meetupService.getCalendarList(userInfoDto.getId(), channelUserList)).willReturn(calendarResponseDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/calendar"))
                .andExpect(status().isOk())
                .andDo(document("calendar-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("???????????? ????????? ID"),
                                fieldWithPath("[].userName").description("???????????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void getMeetupByUserId() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<MeetupResponseDto> meetupResponseDtoList = new ArrayList<>();
        meetupResponseDtoList.add(MeetupResponseDto.builder()
                .id(11L)
                .title("meetupTitle")
                .color("meetupColor")
                .channelName("meetupChannelName")
                .build());
        given(meetupService.getResponseDtoList(anyString())).willReturn(meetupResponseDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup"))
                .andExpect(status().isOk())
                .andDo(document("meetup-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("????????? ?????? ID"),
                                fieldWithPath("[].title").description("????????? ??????"),
                                fieldWithPath("[].color").description("????????? ??????"),
                                fieldWithPath("[].channelName").description("????????? ?????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void getUserListByMeetup() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        Channel channel = Channel.builder()
                .id("channelId")
                .name("channelName")
                .displyName("channelDisplayName")
                .type(ChannelType.Open)
                .team(Team.builder().build())
                .build();
        given(meetupService.getMeetupChannelById(anyLong())).willReturn(channel);

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();
        userInfoDtoList.add(userInfoDto);
        given(channelUserService.getMeetupUserByChannel(any(Channel.class), anyString())).willReturn(userInfoDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/users/11"))
                .andExpect(status().isOk())
                .andDo(document("meetup-users-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("????????? ???????????? ????????? ??????Id"),
                                fieldWithPath("[].nickname").description("????????? ???????????? ????????? ?????????")
                        )
                ));
    }

    @Test
    public void getUserByTeam() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        String mmSessionToken = "mmSessionToken";
        given(authService.getMMSessionToken(anyString())).willReturn(mmSessionToken);

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();
        userInfoDtoList.add(userInfoDto);
        given(teamUserService.getUserByTeam(anyString(), anyString())).willReturn(userInfoDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/userList/teamId"))
                .andExpect(status().isOk())
                .andDo(document("users-in-team-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("?????? ???????????? ????????? ?????? ID"),
                                fieldWithPath("[].nickname").description("?????? ???????????? ????????? ?????????")
                        )
                ));
    }

    @Test
    public void createNewChannel() throws Exception {
        ChannelCreateRequestDto channelCreateRequestDto = ChannelCreateRequestDto.builder()
                .teamId("teamId")
                .name("channelName")
                .displayName("channelDisplayName")
                .type(ChannelType.Open)
                .inviteList(new ArrayList<>())
                .build();

        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        String mmSessionToken = "mmSessionToken";
        given(authService.getMMSessionToken(anyString())).willReturn(mmSessionToken);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/meetup/channel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channelCreateRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("channel-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("teamId").description("????????? ????????? ?????? ?????? ?????? ID"),
                                fieldWithPath("name").description("????????? ????????? ??????"),
                                fieldWithPath("displayName").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("type").description("????????? ????????? ?????? (Open, private, Direct, Group)"),
                                fieldWithPath("inviteList").description("????????? ????????? ????????? ????????? ??????")
                        )
                ));
    }

    @Test
    public void getAllTeamByUserId() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        List<TeamActivateResponseDto> teamActivateResponseDtoList = new ArrayList<>();
        teamActivateResponseDtoList.add(TeamActivateResponseDto.builder()
                .id("teamId")
                .displayName("teamDisplayName")
                .isActivate(false)
                .build());
        given(teamUserService.getActivateTeamByUser(anyString())).willReturn(teamActivateResponseDtoList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/meetup/team/activate"))
                .andExpect(status().isOk())
                .andDo(document("team-all-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("???????????? ????????? ?????? ?????? ID"),
                                fieldWithPath("[].displayName").description("???????????? ????????? ?????? ??????"),
                                fieldWithPath("[].isActivate").description("???????????? ????????? ?????? ???????????????")
                        )
                ));
    }

    @Test
    public void activateTeamById() throws Exception {
        List<TeamActivateRequestDto> teamActivateRequestDtoList = new ArrayList<>();
        teamActivateRequestDtoList.add(TeamActivateRequestDto.builder()
                .teamId("teamId")
                .build());

        UserInfoDto userInfoDto = new UserInfoDto("userId", "userNickname");
        given(authService.getMyInfoSecret()).willReturn(userInfoDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/meetup/team/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamActivateRequestDtoList)))
                .andExpect(status().isOk())
                .andDo(document("team-activate-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].teamId").description("?????????/???????????? ????????? ?????? ?????? ID")
                        )
                ));
    }
}
