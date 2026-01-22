package com.MohdTauseefKhan.main.Services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MohdTauseefKhan.main.Repositories.TournamentRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository repository;

    public int createTournament(String name, String sport,
                            String start, String end, int totalTeams) {

    return repository.saveTournament(name, sport, start, end, totalTeams);
}


    public List<Map<String, Object>> getTournaments() {
        return repository.getAllTournaments();
    }

    public void addTeam(String team, String college, int tournamentId) {
        repository.saveTeam(team, college, tournamentId);
    }

    public void createMatch(int tournamentId, int t1, int t2, String date) {
        repository.scheduleMatch(tournamentId, t1, t2, date);
    }

    public List<Map<String, Object>> getSchedule(int tournamentId) {
        return repository.getSchedule(tournamentId);
    }

    public void addScore(int matchId, int s1, int s2, int winner) {
        repository.saveScore(matchId, s1, s2, winner);
    }

    public List<Map<String, Object>> getResults() {
        return repository.getResults();
    }


    
    // Check if team can be added
    public boolean canAddTeam(int tournamentId) {
        int required = repository.getTotalTeams(tournamentId);
        int added = repository.countTeams(tournamentId);
        String status = repository.getTournamentStatus(tournamentId);

        return added < required && "CREATED".equals(status);
    }

    public void addTeam(int tournamentId, String teamName, String department) {

    repository.addTeam(teamName, department, tournamentId);

    int added = repository.countTeams(tournamentId);
    int total = repository.getTotalTeams(tournamentId);

    if (added == total) {
        repository.updateTournamentStatus(tournamentId, "TEAMS_FINALIZED");
    }
}


    // Finalize teams
    public void finalizeTeams(int tournamentId) {
        int required = repository.getTotalTeams(tournamentId);
        int added = repository.countTeams(tournamentId);

        if (added != required) {
            throw new IllegalStateException("All teams must be added before finalizing");
        }

        repository.updateTournamentStatus(tournamentId, "TEAMS_FINALIZED");
    }

    

    // Get teams (for UI)
    public List<Map<String, Object>> getTeams(int tournamentId) {
        return repository.getTeams(tournamentId);
    }


    public List<Map<String, Object>> getTournamentsWithActions() {

    List<Map<String, Object>> tournaments =
            repository.getAllTournamentsWithTeamCount();

    for (Map<String, Object> t : tournaments) {

        int totalTeams = ((Number) t.get("total_teams")).intValue();
        int addedTeams = ((Number) t.get("added_teams")).intValue();
        String status = (String) t.get("status");

        boolean canGenerate =
                addedTeams == totalTeams &&
                "TEAMS_FINALIZED".equals(status);

        t.put("canGenerate", canGenerate);
    }

    return tournaments;
}


public int getTotalTeams(int tournamentId) {
    return repository.getTotalTeams(tournamentId);
}

public boolean canGenerateSchedule(int tournamentId) {
        String status = repository.getTournamentStatus(tournamentId);
        return "TEAMS_FINALIZED".equals(status);
    }

    public void updateMatchScore(int matchId, int team1Score, int team2Score) {

    Map<String, Object> match = repository.getMatchTeams(matchId);

    int team1Id = ((Number) match.get("team1_id")).intValue();
    int team2Id = ((Number) match.get("team2_id")).intValue();

    int winnerTeamId;

    if (team1Score > team2Score) {
        winnerTeamId = team1Id;
    } else if (team2Score > team1Score) {
        winnerTeamId = team2Id;
    } else {
        winnerTeamId = 0; // draw
    }

    repository.saveScore(matchId, team1Score, team2Score, winnerTeamId);
}


public List<Map<String, Object>> getAllMatches() {
    return repository.getAllMatches();
}

public List<Map<String, Object>> getAllTournaments() {
    return repository.getAllTournaments();
}

public List<Map<String, Object>> getAllMatchesWithTournament() {
    return repository.getAllMatchesWithTournament();
}

}

