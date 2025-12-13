package com.ssafy.crewup.domain.board.controller;

import com.ssafy.crewup.domain.board.service.XXXService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/xxxs")
@RequiredArgsConstructor
public class XXXController {

    private final XXXService service;
}