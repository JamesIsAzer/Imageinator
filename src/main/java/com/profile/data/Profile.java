package com.profile.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    public String tag;
    public String name;
    public int townHallLevel;
    public int expLevel;
    public int trophies;
    public int bestTrophies;
    public int warStars;
    public int attackWins;
    public int defenseWins;
    public String role;
    public int donations;
    public int donationsReceived;
    public Clan clan;
    public League leagueTier;
    public LegendStatistics legendStatistics;
    public Achievement[] achievements;
    public Unit[] troops;
    public Unit[] spells;
    public Unit[] heroes;
}
