package com.MohdTauseefKhan.main.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MohdTauseefKhan.main.Services.TournamentService;

@Controller
public class TeamController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/teams/add")
    public String addTeamsPage(
            @RequestParam int tournamentId,
            Model model) {

        // REQUIRED DATA
        List<Map<String, Object>> teams =
                tournamentService.getTeams(tournamentId);

        int totalTeams =
                tournamentService.getTotalTeams(tournamentId);

        boolean canAdd =
                tournamentService.canAddTeam(tournamentId);

        boolean canGenerate =
                tournamentService.canGenerateSchedule(tournamentId);

        // SEND TO UI
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("teams", teams);
        model.addAttribute("totalTeams", totalTeams);
        model.addAttribute("canAdd", canAdd);
        model.addAttribute("canGenerate", canGenerate);

        return "add-teams";
    }


    @PostMapping("/teams/add")
    public String addTeam(
            @RequestParam int tournamentId,
            @RequestParam String teamName,
            @RequestParam String department) {

        tournamentService.addTeam(tournamentId, teamName, department);

        return "redirect:/teams/add?tournamentId=" + tournamentId;
    }

    @GetMapping("/teams/finalize")
    public String finalizeTeams(@RequestParam int tournamentId) {
        tournamentService.finalizeTeams(tournamentId);
        return "redirect:/tournaments";
    }
}

