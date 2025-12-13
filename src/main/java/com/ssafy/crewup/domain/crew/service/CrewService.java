package com.ssafy.crewup.domain.crew.service;

import com.ssafy.crewup.domain.crew.mapper.CrewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private final CrewMapper crewMapper;
}