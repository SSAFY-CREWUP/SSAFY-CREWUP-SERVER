package com.ssafy.crewup.domain.vote.service;

import com.ssafy.crewup.domain.vote.mapper.VoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteMapper voteMapper;
}