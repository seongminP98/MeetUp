package com.meetup.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * created by seongmin on 2022/10/25
 * updated by seongmin on 2022/11/10
 */
@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {

    USER_NOT_FOUND(BAD_REQUEST, "40001", "사용자를 찾을 수 없습니다."),

    BAD_REQUEST_LOGOUT(BAD_REQUEST, "40002", "잘못된 로그아웃 접근입니다."),

    TEAM_NOT_FOUND(BAD_REQUEST, "40003", "해당 팀을 찾을 수 없습니다."),

    CHANNEL_NOT_FOUND(BAD_REQUEST, "40004", "해당 채널을 찾을 수 없습니다."),

    MEETUP_NOT_FOUND(BAD_REQUEST, "40005", "해당 밋업을 찾을 수 없습니다."),

    SCHEDULE_NOT_FOUND(BAD_REQUEST, "40006", "해당 스케줄(개인일정)을 찾을 수 없습니다."),

    MEETING_NOT_FOUND(BAD_REQUEST, "40007", "해당 미팅(밋업 신청 스케쥴)을 찾을 수 없습니다."),

    KEY_NOT_MATCHING(BAD_REQUEST, "40008", "키 값이 일치하지 않습니다."),

    DATE_FORMAT_EX(BAD_REQUEST, "40009", "date의 형식은 yyyy-MM-dd HH:mm:ss 입니다."),

    MM_BAD_REQUEST(BAD_REQUEST, "40010", "잘못된 mattermost api 요청입니다."),

    TOO_SHORT_DURATION(BAD_REQUEST, "40011", "시작시간과 종료시각의 차이가 30분 이하입니다."),

    TEAM_USER_NOT_FOUND(BAD_REQUEST, "40012", "유저가 속한 팀 정보를 찾을 수 없습니다."),
    PARTY_NOT_FOUND(BAD_REQUEST, "40013", "해당 그룹을 찾을 수 없습니다."),

    EMPTY_CREDENTIAL(UNAUTHORIZED, "40101", "인증 정보가 없습니다."),

    EMPTY_MM_CREDENTIAL(UNAUTHORIZED, "40102", "메터모스트 인증 정보가 없습니다."),

    ACCESS_DENIED(FORBIDDEN, "40301", "권한이 없습니다."),

    ACCESS_DENIED_THIS_SCHEDULE(FORBIDDEN, "40302", "해당 스케줄을 볼 권한이 없습니다."),

    ID_PWD_NOT_MATCHING(FORBIDDEN, "40303", "아이디 또는 패스워드가 일치하지 않습니다."),

    ADMIN_ACCESS_DENIED(FORBIDDEN, "40304", "관리자 권한이 없습니다. 관리자만 이용할 수 있습니다."),

    CHANNEL_ACCESS_DENIED(FORBIDDEN, "40305", "해당 채널에 대한 권한이 없습니다."),

    MEETUP_ACCESS_DENIED(FORBIDDEN, "40306", "해당 밋업 작업에 대한 권한이 없습니다."),

    MM_FORBIDDEN(FORBIDDEN, "40307", "해당 mattermost api 권한이 없습니다."),
    PARTY_ACCESS_DENIED(FORBIDDEN, "40308", "해당 그룹에 대한 권한이 없습니다."),

    DUPLICATE_NICKNAME(CONFLICT, "40901", "닉네임이 중복됩니다."),

    DUPLICATE_ID(CONFLICT, "40902", "아이디가 중복됩니다."),

    DUPLICATE_INSERT_DATETIME(CONFLICT, "40903", "기존에 있는 일정과 중복되어 등록이 불가합니다."),

    DUPLICATE_UPDATE_DATETIME(CONFLICT, "40904", "기존에 있는 일정과 중복되어 수정이 불가합니다."),

    DUPLICATE_CHANNEL_NAME(CONFLICT, "40905", "기존에 있는 채널네임이 중복되어 새로운 채널생성이 불가능합니다."),

    DUPLICATE_MEETUP(CONFLICT, "40906", "해당 채널은 이미 생성된 밋업이 있습니다."),
    DUPLICATE_GROUP(CONFLICT, "40907", "그룹(내가 리더인 그룹) 이름이 중복됩니다."),

    MEETUP_DELETED(CONFLICT, "41001", "삭제된 밋업의 일정은 수정할 수 없습니다. 밋업관리자에게 문의해주세요."),

    MATTERMOST_EXCEPTION(INTERNAL_SERVER_ERROR, "50001", "매터모스트 에러, 잠시 후 다시 시도해주세요."),
    PASSWORD_DECRYPTION_ERROR(INTERNAL_SERVER_ERROR, "50002", "비밀번호 복호화 에러");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
