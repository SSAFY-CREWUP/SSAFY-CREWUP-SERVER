package com.ssafy.crewup.vote.service.impl;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.notification.event.NotificationEvent;
import com.ssafy.crewup.vote.Vote;
import com.ssafy.crewup.vote.VoteOption;
import com.ssafy.crewup.vote.VoteRecord;
import com.ssafy.crewup.vote.dto.request.VoteCreateRequest;
import com.ssafy.crewup.vote.dto.response.VoteResponse;
import com.ssafy.crewup.vote.dto.response.VoteResultResponse;
import com.ssafy.crewup.vote.mapper.VoteMapper;
import com.ssafy.crewup.vote.mapper.VoteOptionMapper;
import com.ssafy.crewup.vote.mapper.VoteRecordMapper;
import com.ssafy.crewup.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final VoteOptionMapper voteOptionMapper;
    private final VoteRecordMapper voteRecordMapper;
    private final CrewMemberMapper crewMemberMapper;
    private final CrewMapper crewMapper;  // ⭐ 추가
    private final ApplicationEventPublisher eventPublisher;  // ⭐ 추가

    @Override
    @Transactional
    public void createVote(Long userId, Long crewId, VoteCreateRequest request) {
        // 1. 매니저 권한 검증
        validateManagerAuthority(userId, crewId);

        // 2. 투표 본체 생성
        Vote vote = Vote.builder()
                .crewId(crewId)
                .creatorId(userId)
                .title(request.title())
                .endAt(request.endAt())
                .multipleChoice(Objects.requireNonNullElse(request.multipleChoice(), false))
                .isAnonymous(Objects.requireNonNullElse(request.isAnonymous(), false))
                .limitCount(Objects.requireNonNullElse(request.limitCount(), 0))
                .build();
        voteMapper.insert(vote);

        // 3. 투표 선택지 생성 (최대 5개 제한은 컨트롤러 @Size로 검증)
        for (String content : request.options()) {
            voteOptionMapper.insert(VoteOption.builder()
                    .voteId(vote.getId())
                    .content(content)
                    .count(0)
                    .build());
        }

        // ⭐ 4. 투표 생성 알림 발송
        sendVoteCreatedNotification(vote, userId);

        log.info("투표 생성 완료 - voteId: {}, crewId: {}, userId: {}",
                vote.getId(), crewId, userId);
    }

    @Override
    @Transactional
    public void castVote(Long userId, Long voteId, List<Long> optionIds) {
        Vote vote = voteMapper.findById(voteId);
        if (vote == null) throw new CustomException(ErrorCode.NOT_FOUND);

        // 마감 시간 확인
        if (vote.getEndAt() != null && vote.getEndAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.BAD_REQUEST); // 마감된 투표
        }

        // 중복 선택 여부 확인
        if (!vote.getMultipleChoice() && optionIds.size() > 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        for (Long optionId : optionIds) {
            // 비관적 락 적용 (FOR UPDATE)
            VoteOption option = voteOptionMapper.findByIdWithLock(optionId);

            // 인원 제한 확인 (선착순)
            if (vote.getLimitCount() != null && vote.getLimitCount() > 0 && option.getCount() >= vote.getLimitCount()) {
                throw new CustomException(ErrorCode.BAD_REQUEST); // "인원 제한 초과"
            }

            // 투표 기록 삽입 (DB Unique 제약조건으로 1인 1투표/옵션 보장)
            VoteRecord record = VoteRecord.builder()
                    .userId(userId)
                    .voteId(voteId)
                    .voteOptionId(optionId)
                    .build();
            voteRecordMapper.insert(record);

            // 카운트 증가
            voteOptionMapper.incrementCount(optionId);
        }

        log.info("투표 완료 - userId: {}, voteId: {}, options: {}", userId, voteId, optionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public VoteResultResponse getVoteResult(Long userId, Long voteId) {
        Vote vote = voteMapper.findById(voteId);
        if (vote == null) throw new CustomException(ErrorCode.NOT_FOUND);

        // 투표 참여자만 결과 조회 가능
        List<VoteRecord> userRecords = voteRecordMapper.findByUserId(userId);
        boolean participated = userRecords.stream().anyMatch(r -> r.getVoteId().equals(voteId));
        if (!participated) {
            throw new CustomException(ErrorCode.FORBIDDEN); // 투표를 해야 결과를 볼 수 있음
        }

        List<VoteOption> options = voteOptionMapper.findByVoteId(voteId);
        List<VoteResultResponse.OptionDetail> optionDetails = options.stream().map(option -> {
            // 무기명 투표가 아닐 경우에만 투표자 명단 포함 (시간 순 정렬)
            List<VoteResultResponse.VoterInfo> voters = vote.getIsAnonymous() ? List.of() :
                    voteRecordMapper.findVotersByOptionId(option.getId());

            return new VoteResultResponse.OptionDetail(
                    option.getId(),
                    option.getContent(),
                    option.getCount(),
                    voters
            );
        }).collect(Collectors.toList());

        return new VoteResultResponse(
                vote.getId(),
                vote.getTitle(),
                vote.getIsAnonymous(),
                optionDetails
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoteResponse> getActiveVotes(Long crewId) {
        return voteMapper.findActiveVotes(crewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoteResponse> getEndedVotes(Long crewId) {
        return voteMapper.findEndedVotes(crewId);
    }

    @Override
    @Transactional
    public void closeVote(Long userId, Long voteId) {
        Vote vote = voteMapper.findById(voteId);
        if (!vote.getCreatorId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        voteMapper.closeVote(voteId);

        // ⭐ 투표 마감 알림 발송
        sendVoteClosedNotification(vote);

        log.info("투표 마감 - voteId: {}, userId: {}", voteId, userId);
    }

    @Override
    @Transactional
    public void deleteVote(Long userId, Long voteId) {
        Vote vote = voteMapper.findById(voteId);
        if (vote == null) throw new CustomException(ErrorCode.NOT_FOUND);

        // 작성자만 삭제 가능
        if (!vote.getCreatorId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        voteMapper.delete(voteId);

        log.info("투표 삭제 - voteId: {}, userId: {}", voteId, userId);
    }

    // ==================== 알림 발송 메서드 ====================

    /**
     * 투표 생성 알림 발송
     */
    private void sendVoteCreatedNotification(Vote vote, Long excludeUserId) {
        try {
            Crew crew = crewMapper.findById(vote.getCrewId());
            if (crew == null) {
                log.warn("크루를 찾을 수 없음 - crewId: {}", vote.getCrewId());
                return;
            }

            String content = String.format("새로운 투표 '%s'이(가) 등록되었습니다.", vote.getTitle());
            String url = String.format("/vote/%d", vote.getId());

            NotificationEvent event = NotificationEvent.builder()
                    .crewId(vote.getCrewId())
                    .crewName(crew.getName())
                    .excludeUserId(excludeUserId)  // 생성자 제외
                    .type(NotificationType.VOTE)
                    .content(content)
                    .url(url)
                    .build();

            eventPublisher.publishEvent(event);

            log.debug("투표 생성 알림 이벤트 발행 - voteId: {}, title: {}",
                    vote.getId(), vote.getTitle());

        } catch (Exception e) {
            log.error("투표 생성 알림 발송 실패 - voteId: {}", vote.getId(), e);
        }
    }

    /**
     * 투표 마감 알림 발송
     */
    private void sendVoteClosedNotification(Vote vote) {
        try {
            Crew crew = crewMapper.findById(vote.getCrewId());
            if (crew == null) {
                log.warn("크루를 찾을 수 없음 - crewId: {}", vote.getCrewId());
                return;
            }

            String content = String.format("투표 '%s'이(가) 마감되었습니다.", vote.getTitle());
            String url = String.format("/vote/%d", vote.getId());

            NotificationEvent event = NotificationEvent.builder()
                    .crewId(vote.getCrewId())
                    .crewName(crew.getName())
                    .excludeUserId(null)  // 모든 멤버에게 알림
                    .type(NotificationType.VOTE)
                    .content(content)
                    .url(url)
                    .build();

            eventPublisher.publishEvent(event);

            log.debug("투표 마감 알림 이벤트 발행 - voteId: {}, title: {}",
                    vote.getId(), vote.getTitle());

        } catch (Exception e) {
            log.error("투표 마감 알림 발송 실패 - voteId: {}", vote.getId(), e);
        }
    }

    /**
     * 권한 검증 헬퍼 메서드
     */
    private void validateManagerAuthority(Long userId, Long crewId) {
        List<CrewMember> members = crewMemberMapper.findByCrewId(crewId);

        CrewMember currentMember = members.stream()
                .filter(m -> java.util.Objects.equals(m.getUserId(), userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (currentMember.getRole() == com.ssafy.crewup.enums.CrewMemberRole.MEMBER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}