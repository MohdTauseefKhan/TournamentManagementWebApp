package com.MohdTauseefKhan.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MohdTauseefKhan.main.Services.ScheduleService;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedule/generate")
    public String generateSchedule(@RequestParam int tournamentId) {

        scheduleService.generateSchedule(tournamentId);

        return "redirect:/schedule?tournamentId=" + tournamentId;
    }
}

