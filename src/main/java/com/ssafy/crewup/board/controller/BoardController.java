package com.ssafy.crewup.board.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
