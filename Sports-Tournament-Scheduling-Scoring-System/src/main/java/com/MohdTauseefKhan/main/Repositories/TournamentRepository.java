package com.MohdTauseefKhan.main.Repositories;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.time.LocalDate;


@Repository
public class TournamentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Insert Tournament
    public int saveTournament(String name, String sport,
                          String start, String end, int totalTeams) {

    String sql = """
        INSERT INTO tournament (name, sport, start_date, end_date, total_teams)
        VALUES (?, ?, ?, ?, ?)
    """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        ps.setString(2, sport);
        ps.setString(3, start);
        ps.setString(4, end);
        ps.setInt(5, totalTeams);
        return ps;
    }, keyHolder);

    return keyHolder.getKey().intValue();
}



    // Fetch All Tournaments
    public List<Map<String, Object>> getAllTournaments() {
        String sql = "SELECT * FROM tournament";
        return jdbcTemplate.queryForList(sql);
    }

    // Insert Team
    public void saveTeam(String teamName, String college, int tournamentId) {
        String sql = "INSERT INTO team (team_name, college_name, tournament_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, teamName, college, tournamentId);
    }

    // Schedule Match
    public void scheduleMatch(int tournamentId, int team1, int team2, String date) {
        String sql = """
            INSERT INTO match_schedule (tournament_id, team1_id, team2_id, match_date)
            VALUES (?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, tournamentId, team1, team2, date);
    }

    // Get Match Schedule (JOIN)
    public List<Map<String, Object>> getSchedule(int tournamentId) {
        String sql = """
            SELECT 
                m.match_id,
                t1.team_name AS team1,
                t2.team_name AS team2,
                m.match_date
            FROM match_schedule m
            JOIN team t1 ON m.team1_id = t1.team_id
            JOIN team t2 ON m.team2_id = t2.team_id
            WHERE m.tournament_id = ?
        """;
        return jdbcTemplate.queryForList(sql, tournamentId);
    }



    // Results View (JOIN)
    public List<Map<String, Object>> getResults() {
        String sql = """
            SELECT 
                t1.team_name AS team1,
                s.team1_score,
                t2.team_name AS team2,
                s.team2_score,
                tw.team_name AS winner
            FROM score s
            JOIN match_schedule m ON s.match_id = m.match_id
            JOIN team t1 ON m.team1_id = t1.team_id
            JOIN team t2 ON m.team2_id = t2.team_id
            JOIN team tw ON s.winner_team_id = tw.team_id
        """;
        return jdbcTemplate.queryForList(sql);
    }




    // Get total teams required for tournament
public int getTotalTeams(int tournamentId) {
    String sql = "SELECT total_teams FROM tournament WHERE tournament_id = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, tournamentId);
}

// Count how many teams are already added
public int countTeams(int tournamentId) {
    String sql = "SELECT COUNT(*) FROM team WHERE tournament_id = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, tournamentId);
}

// Add team
public void addTeam(String teamName, String department, int tournamentId) {
    String sql = """
        INSERT INTO team (team_name, department, tournament_id)
        VALUES (?, ?, ?)
    """;
    jdbcTemplate.update(sql, teamName, department, tournamentId);
}

// Update tournament status
public void updateTournamentStatus(int tournamentId, String status) {
    String sql = "UPDATE tournament SET status = ? WHERE tournament_id = ?";
    jdbcTemplate.update(sql, status, tournamentId);
}

// Get tournament status
public String getTournamentStatus(int tournamentId) {
    String sql = "SELECT status FROM tournament WHERE tournament_id = ?";
    return jdbcTemplate.queryForObject(sql, String.class, tournamentId);
}

// Get teams for a tournament
public List<Map<String, Object>> getTeams(int tournamentId) {
    String sql = "SELECT * FROM team WHERE tournament_id = ?";
    return jdbcTemplate.queryForList(sql, tournamentId);
}



// Get all team IDs of a tournament
public List<Integer> getTeamIds(int tournamentId) {
    String sql = "SELECT team_id FROM team WHERE tournament_id = ?";
    return jdbcTemplate.queryForList(sql, Integer.class, tournamentId);
}

// Insert a match
public void insertMatch(int tournamentId, int team1Id, int team2Id) {
    String sql = """
        INSERT INTO match_schedule (tournament_id, team1_id, team2_id)
        VALUES (?, ?, ?)
    """;
    jdbcTemplate.update(sql, tournamentId, team1Id, team2Id);
}


public List<Map<String, Object>> getAllTournamentsWithTeamCount() {
    String sql = """
        SELECT 
            t.tournament_id,
            t.name,
            t.sport,
            t.total_teams,
            t.status,
            COUNT(tm.team_id) AS added_teams
        FROM tournament t
        LEFT JOIN team tm ON t.tournament_id = tm.tournament_id
        GROUP BY t.tournament_id
    """;

    return jdbcTemplate.queryForList(sql);
}

public void insertMatch(int tournamentId, int team1, int team2, LocalDate matchDate) {
    String sql = """
        INSERT INTO match_schedule
        (tournament_id, team1_id, team2_id, match_date)
        VALUES (?, ?, ?, ?)
    """;
    jdbcTemplate.update(sql, tournamentId, team1, team2, matchDate);
}

public LocalDate getStartDate(int tournamentId) {
    String sql = "SELECT start_date FROM tournament WHERE tournament_id = ?";
    return jdbcTemplate.queryForObject(sql, LocalDate.class, tournamentId);
}

public LocalDate getEndDate(int tournamentId) {
    String sql = "SELECT end_date FROM tournament WHERE tournament_id = ?";
    return jdbcTemplate.queryForObject(sql, LocalDate.class, tournamentId);
}

public void saveScore(int matchId, int team1Score, int team2Score, int winnerTeamId) {

    String sql = """
        INSERT INTO score (match_id, team1_score, team2_score, winner_team_id)
        VALUES (?, ?, ?, ?)
    """;

    jdbcTemplate.update(sql, matchId, team1Score, team2Score, winnerTeamId);
}


public List<Map<String, Object>> getAllMatches() {
    String sql = """
        SELECT m.match_id,
               t1.team_name AS team1,
               t2.team_name AS team2
        FROM match_schedule m
        JOIN team t1 ON m.team1_id = t1.team_id
        JOIN team t2 ON m.team2_id = t2.team_id
    """;
    return jdbcTemplate.queryForList(sql);
}


public List<Map<String, Object>> getAllMatchesWithTournament() {

    String sql = """
        SELECT 
            m.match_id,
            m.tournament_id,
            t1.team_name AS team1,
            t2.team_name AS team2,
            m.match_date
        FROM match_schedule m
        JOIN team t1 ON m.team1_id = t1.team_id
        JOIN team t2 ON m.team2_id = t2.team_id
        LEFT JOIN score s ON m.match_id = s.match_id
        WHERE s.match_id IS NULL
        ORDER BY m.match_date
    """;

    return jdbcTemplate.queryForList(sql);
}



public Map<String, Object> getMatchTeams(int matchId) {

    String sql = """
        SELECT team1_id, team2_id
        FROM match_schedule
        WHERE match_id = ?
    """;

    return jdbcTemplate.queryForMap(sql, matchId);
}


}

