package com.ssafy.crewup.domain.board.service;

import com.ssafy.crewup.domain.board.mapper.XXXMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class XXXService {

    private final XXXMapper mapper;
}