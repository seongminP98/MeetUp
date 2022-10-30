package com.meetup.backend.repository.channel;

import com.meetup.backend.entity.channel.Channel;
import com.meetup.backend.entity.channel.ChannelUser;
import com.meetup.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * created by seongmin on 2022/10/21
 * updated by seungyong on 2022/10/23
 */
public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    List<ChannelUser> findByUser(User user);

    Optional<ChannelUser> findByChannelAndUser(Channel channel, User user);

}
