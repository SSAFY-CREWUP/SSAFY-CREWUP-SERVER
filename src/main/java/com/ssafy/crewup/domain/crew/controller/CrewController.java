package com.ssafy.crewup.domain.crew.controller;

import com.ssafy.crewup.domain.crew.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;
}