package com.eldarion.barbariaplugin.factions;

import org.json.simple.JSONArray;
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
        JSONObject condition_segment = new JSONObject();
        JSONObject reward_segment = new JSONObject();
        for (Condition c : conditions) {
            condition_segment.put(c.type.name(), c.value);
        }
        for (Condition c : rewards) {
            reward_segment.put(c.type.name(), c.value);
        }
        object.put("Conditions", condition_segment);
        object.put("Rewards", reward_segment);
        object.put("dead_line", deadLine);
        object.put("execute_interval", executeInterval);
        object.put("condition_faction", conditionFaction.getFactionName());
        object.put("reward_faction", rewardFaction.getFactionName());
    }

    public void addCondition (Conditions condition) {

    }

    public void addReward (Conditions condition) {

    }

    double executeInterval;
    double deadLine;
    Faction conditionFaction;
    Faction rewardFaction;
    List<Condition> conditions = new ArrayList<Condition>();
    List<Condition> rewards = new ArrayList<Condition>();
}
