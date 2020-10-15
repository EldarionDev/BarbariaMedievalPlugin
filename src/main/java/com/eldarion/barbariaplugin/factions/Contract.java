package com.eldarion.barbariaplugin.factions;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

class Condition {
    public double value;
    Contract.Conditions type;
}

public class Contract {

    enum Conditions {
        GOLD, NAP
    }

    public Contract () {

    }

    public void load () {

    }

    public void save (JSONObject object) {

    }

    public void addCondition (Conditions condition) {

    }

    public void addReward (Conditions condition) {

    }

    double executeInterval;
    Faction conditionFaction;
    Faction rewardFaction;
    List<Condition> conditions = new ArrayList<Condition>();
    List<Condition> rewards = new ArrayList<Condition>();
}
