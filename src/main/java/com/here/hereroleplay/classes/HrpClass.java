package com.here.hereroleplay.classes;

import com.here.hereroleplay.data.PlayerProfile;

public class HrpClass {

    private final String name;
    private final String description;
    
    // Requirements
    private final int reqStrength;
    private final int reqAgility;
    private final int reqVitality;
    private final int reqIntelligence;
    private final int reqTotalPoints;

    public HrpClass(String name, String description, int reqStrength, int reqAgility, int reqVitality, int reqIntelligence, int reqTotalPoints) {
        this.name = name;
        this.description = description;
        this.reqStrength = reqStrength;
        this.reqAgility = reqAgility;
        this.reqVitality = reqVitality;
        this.reqIntelligence = reqIntelligence;
        this.reqTotalPoints = reqTotalPoints;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public int getReqStrength() { return reqStrength; }
    public int getReqAgility() { return reqAgility; }
    public int getReqVitality() { return reqVitality; }
    public int getReqIntelligence() { return reqIntelligence; }
    public int getReqTotalPoints() { return reqTotalPoints; }

    public boolean meetsRequirements(PlayerProfile profile) {
        if (profile.getStrengthPoints() < reqStrength) return false;
        if (profile.getAgilityPoints() < reqAgility) return false;
        if (profile.getVitalityPoints() < reqVitality) return false;
        if (profile.getIntelligencePoints() < reqIntelligence) return false;
        
        int totalPoints = profile.getStrengthPoints() + profile.getAgilityPoints() + 
                          profile.getVitalityPoints() + profile.getIntelligencePoints();
        
        return totalPoints >= reqTotalPoints;
    }
}
