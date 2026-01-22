package com.MohdTauseefKhan.main.Services;

import java.time.LocalDate;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MohdTauseefKhan.main.Repositories.TournamentRepository;

@Service
public class ScheduleService {

    @Autowired
    private TournamentRepository repository;

    public void generateSchedule(int tournamentId) {

    if (!canGenerateSchedule(tournamentId)) {
        throw new IllegalStateException("Schedule cannot be generated");
    }

    List<Integer> teamIds = repository.getTeamIds(tournamentId);
    Collections.shuffle(teamIds); // unbiased random

    LocalDate start = repository.getStartDate(tournamentId);
    LocalDate end = repository.getEndDate(tournamentId);

    List<LocalDate> dates = new ArrayList<>();
    for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
        dates.add(d);
    }

    Collections.shuffle(dates); // random date allocation

    int dateIndex = 0;

    for (int i = 0; i < teamIds.size(); i++) {
        for (int j = i + 1; j < teamIds.size(); j++) {

            LocalDate matchDate = dates.get(dateIndex % dates.size());

            repository.insertMatch(
                    tournamentId,
                    teamIds.get(i),
                    teamIds.get(j),
                    matchDate
            );

            dateIndex++;
        }
    }

    repository.updateTournamentStatus(tournamentId, "SCHEDULE_GENERATED");
}

// Check if schedule can be generated
    public boolean canGenerateSchedule(int tournamentId) {
        String status = repository.getTournamentStatus(tournamentId);
        return "TEAMS_FINALIZED".equals(status);
    }

}

