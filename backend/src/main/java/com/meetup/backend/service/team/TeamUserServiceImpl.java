package com.meetup.backend.service.team;

import com.meetup.backend.dto.team.TeamResponseDto;
import com.meetup.backend.entity.team.Team;
import com.meetup.backend.entity.team.TeamUser;
import com.meetup.backend.entity.user.RoleType;
import com.meetup.backend.entity.user.User;
import com.meetup.backend.exception.ApiException;
import com.meetup.backend.exception.ExceptionEnum;
import com.meetup.backend.repository.team.TeamUserRepository;
import com.meetup.backend.repository.user.UserRepository;
import com.meetup.backend.service.Client;
import com.meetup.backend.service.user.UserService;
import com.meetup.backend.util.converter.JsonConverter;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.Pager;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * created by myeongseok on 2022/10/21
 * updated by seongmin on 2022/10/30
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TeamUserServiceImpl implements TeamUserService {

    @Autowired
    private final TeamUserRepository teamUserRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<TeamResponseDto> getTeamByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();

        for (TeamUser teamUser : teamUserRepository.findByUser(user)) {
            teamResponseDtoList.add(TeamResponseDto.of(teamUser.getTeam()));
        }

        return teamResponseDtoList;
    }

    // db에 저장되어 있지 않은 팀만 TeamUser db 저장
    @Override
    public void registerTeamUserFromMattermost(String mmSessionToken, List<Team> teamList) {

        MattermostClient client = Client.getClient();
        client.setAccessToken(mmSessionToken);

        for (Team team : teamList) {

            for (int k = 0; ; k++) {

                Response mmTeamUserResponse = client.getTeamMembers(team.getId(), Pager.of(k, 200)).getRawResponse();
                JSONArray userArray = JsonConverter.toJsonArray((BufferedInputStream) mmTeamUserResponse.getEntity());
                if (userArray.isEmpty()) break;

                for (int l = 0; l < userArray.length(); l++) {

                    String userId = userArray.getJSONObject(l).getString("user_id");
                    User user = userRepository.findById(userId).orElseGet(
                            () -> userRepository.save(
                                    User.builder()
                                            .id(userId)
                                            .firstLogin(false)
                                            .role(RoleType.Student)
                                            .build()
                            )
                    );
                    if (teamUserRepository.findByTeamAndUser(team, user).isEmpty()) {
                        TeamUser teamUser = TeamUser.builder().team(team).user(user).build();
                        teamUserRepository.save(teamUser);
                    }
                }
            }
        }
    }
}
