package com.example.flashscoreapp.data.model.domain;

import java.util.List;

// Lớp này chứa toàn bộ dữ liệu hiển thị cho một đội trong tab đội hình
public class LineupDisplayData {
    private final String teamName;
    private final String formation;
    private final List<PlayerDisplay> starters;
    private final List<PlayerDisplay> substitutes;

    public LineupDisplayData(String teamName, String formation, List<PlayerDisplay> starters, List<PlayerDisplay> substitutes) {
        this.teamName = teamName;
        this.formation = formation;
        this.starters = starters;
        this.substitutes = substitutes;
    }

    public String getTeamName() { return teamName; }
    public String getFormation() { return formation; }
    public List<PlayerDisplay> getStarters() { return starters; }
    public List<PlayerDisplay> getSubstitutes() { return substitutes; }
}