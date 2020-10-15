package com.eldarion.barbariaplugin.factions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Condition {
    public Condition (Contract.Conditions t, double v) {
        value = v;
        type = t;
    }
    public double value;
    Contract.Conditions type;
}

public class Contract {

    enum Conditions {
        GOLD, NAP
    }

    public Contract(String n) {
        contractName = n;
    }

    public void load () {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("contracts/" + contractName + ".json"));
                executeInterval = (double) jsonObject.get("execute_interval");
                deadLine = (double) jsonObject.get("dead_line");
                conditionFaction = Factions.getFaction(jsonObject.get("condition_faction").toString());
                rewardFaction = Factions.getFaction(jsonObject.get("reward_faction").toString());
                JSONObject condition_section = (JSONObject) jsonObject.get("Conditions");
                JSONObject reward_section = (JSONObject) jsonObject.get("Rewards");
                Iterator keysToCopyIterator = condition_section.keySet().iterator();
                while(keysToCopyIterator.hasNext()) {
                    String key = (String) keysToCopyIterator.next();
                    conditions.add(new Condition(Conditions.valueOf(key), (double) condition_section.get(key)));
                }
                Iterator keysToCopyIteratorRewards = reward_section.keySet().iterator();
                while(keysToCopyIteratorRewards.hasNext()) {
                    String key = (String) keysToCopyIterator.next();
                    conditions.add(new Condition(Conditions.valueOf(key), (double) condition_section.get(key)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
    }

    public void save () {
        JSONObject object = new JSONObject();
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
        try {
            FileWriter jsonFile = new FileWriter("contracts/" + contractName + ".json");
            jsonFile.write(object.toJSONString());
            jsonFile.close();
        }
        catch (IOException e) {

        }
    }

    public void addCondition (Conditions condition, double amplifier) {
        Condition c = new Condition(condition, amplifier);
        conditions.add(c);
    }

    public void addReward (Conditions condition, double amplifier) {
        Condition c = new Condition(condition, amplifier);
        rewards.add(c);
    }

    String contractName;
    double executeInterval;
    double deadLine;
    Faction conditionFaction;
    Faction rewardFaction;
    List<Condition> conditions = new ArrayList<Condition>();
    List<Condition> rewards = new ArrayList<Condition>();
}
