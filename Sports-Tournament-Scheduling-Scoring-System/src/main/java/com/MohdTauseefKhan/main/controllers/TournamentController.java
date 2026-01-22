package com.MohdTauseefKhan.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MohdTauseefKhan.main.Services.TournamentService;

@Controller
public class TournamentController {

    @Autowired
    private TournamentService service;


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/tournaments")
public String tournaments(Model model) {

    model.addAttribute(
        "tournaments",
        service.getTournamentsWithActions()
    );

    return "tournaments";
}


    @PostMapping("/tournament/create")
public String createTournament(
        @RequestParam String name,
        @RequestParam String sport,
        @RequestParam String start,
        @RequestParam String end,
        @RequestParam int totalTeams) {

    int tournamentId = service.createTournament(
            name, sport, start, end, totalTeams);

    return "redirect:/teams/add?tournamentId=" + tournamentId;
}



    @GetMapping("/schedule")
    public String schedule(@RequestParam int tournamentId, Model model) {
        model.addAttribute("schedule", service.getSchedule(tournamentId));
        return "schedule";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("results", service.getResults());
        return "results";
    }

    @GetMapping("/tournaments/create")
    public String createTournamentPage() {
        return "create-tournament";
    }


@GetMapping("/scores/update")
    public String updateScorePage(Model model) {

        model.addAttribute("tournaments",
                service.getAllTournaments());

        model.addAttribute("matches",
                service.getAllMatchesWithTournament());

        return "update-score";
    }
@PostMapping("/scores/update")
    public String saveScore(
            @RequestParam int matchId,
            @RequestParam int team1Score,
            @RequestParam int team2Score) {

        service.updateMatchScore(
                matchId, team1Score, team2Score);

        return "redirect:/results";
    }

}

