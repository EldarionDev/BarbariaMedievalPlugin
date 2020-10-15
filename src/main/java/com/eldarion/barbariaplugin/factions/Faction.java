package com.eldarion.barbariaplugin.factions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Faction {
    public Faction(String name) {
        factionName = name;
        load();
    }

    public void addTrade (String name) {
        factionTrades.add(new Contract(name));
    }

    public Contract getTrade (String name) {
        for (Contract c : factionTrades) {
            if (c.contractName.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    private void load () {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("factions/" + factionName + ".json"));
            JSONArray jsonRequests = (JSONArray) jsonObject.get("requests");
            JSONArray jsonProposers = (JSONArray) jsonObject.get("proposers");
            JSONArray jsonProposals = (JSONArray) jsonObject.get("proposals");
            JSONArray jsonAtWar = (JSONArray) jsonObject.get("at_war");
            JSONArray jsonNAP = (JSONArray) jsonObject.get("nap");
            JSONArray jsonTrades = (JSONArray) jsonObject.get("faction_trades");
            this.trustworthy = (double) jsonObject.get("trustworthy");
            this.aggression = (double) jsonObject.get("aggression");
            this.loyalty = (double) jsonObject.get("loyalty");
            Iterator iterator  = jsonRequests.iterator();
            while (iterator.hasNext()) {
                String myVar = iterator.next().toString();
                requests.put(Bukkit.getPlayer(UUID.fromString(myVar)).getDisplayName(), UUID.fromString(myVar));
            }
            Iterator iterator1 = jsonProposers.iterator();
            Iterator iterator2 = jsonProposals.iterator();
            while (iterator1.hasNext()) {
                UUID proposer = UUID.fromString(iterator1.next().toString());
                String proposal = iterator2.next().toString();
                proposals.put(proposal, proposer);
            }
            Iterator iterator4 = jsonAtWar.iterator();
            while (iterator4.hasNext()) {
                String currentF = iterator4.next().toString();
                factionsAtWar.add(currentF);
            }
            Iterator iterator5 = jsonNAP.iterator();
            while (iterator5.hasNext()) {
                String currentF = iterator5.next().toString();
                factionsNAP.add(currentF);
            }
            Iterator iterator6 = jsonTrades.iterator();
            while (iterator6.hasNext()) {
                String trade = iterator6.next().toString();
                factionTrades.add(new Contract(trade));
            }
        }
        catch(FileNotFoundException e) {

        }
        catch(IOException e) {

        }
        catch(ParseException e) {

        }
    }

    public void save (JSONObject object) {
        JSONObject returnObject;
        JSONArray requests_array = new JSONArray();
        JSONArray proposer_array = new JSONArray();
        JSONArray proposals_array = new JSONArray();
        JSONArray faction_trades = new JSONArray();
        JSONArray factions_at_war_array = new JSONArray();
        JSONArray factions_nap_array = new JSONArray();
        Iterator itR = requests.entrySet().iterator();
        while (itR.hasNext()) {
            Map.Entry pair = (Map.Entry) itR.next();
            requests_array.add(pair.getValue().toString());
        }
        Iterator itP = proposals.entrySet().iterator();
        while (itP.hasNext()) {
            Map.Entry pair = (Map.Entry) itP.next();
            proposer_array.add(pair.getValue().toString());
            proposals_array.add(pair.getKey().toString());
        }
        for (String f1 : factionsAtWar) {
            factions_at_war_array.add(f1);
        }
        for (String f2 : factionsNAP) {
            factions_nap_array.add(f2);
        }
        for (Contract c : factionTrades) {
            faction_trades.add(c.contractName);
            c.save();
        }
        returnObject = object;
        returnObject.put("requests", requests_array);
        returnObject.put("proposers", proposer_array);
        returnObject.put("proposals", proposals_array);
        returnObject.put("trustworthy", this.trustworthy);
        returnObject.put("loyalty", this.loyalty);
        returnObject.put("aggression", this.aggression);
        returnObject.put("at_war", factions_at_war_array);
        returnObject.put("nap", factions_nap_array);
        returnObject.put("faction_trades", faction_trades);
    }

    public void addRequest (Player player) {
        requests.put(player.getDisplayName(), player.getUniqueId());
    }

    public List<String> getRequests () {
        List<String> returnList = new ArrayList<String>();
        Iterator itR = requests.entrySet().iterator();
        while (itR.hasNext()) {
            Map.Entry pair = (Map.Entry) itR.next();
            returnList.add(pair.getKey().toString());
        }
        return returnList;
    }

    public void acceptRequest (String playerName) {
        requests.remove(playerName);
    }

    public void declineRequest (String playerName) {
        requests.remove(playerName);
    }

    public void addProposal (String proposal, UUID member) {
        proposals.put(proposal, member);
    }

    public void removeProposal (UUID member) {
        Iterator itP = proposals.entrySet().iterator();
        while (itP.hasNext()) {
            Map.Entry pair = (Map.Entry) itP.next();
            if (pair.getValue().toString().equalsIgnoreCase(member.toString())) {
                proposals.remove(pair.getKey());
            }
        }
    }

    public List<String> getProposals () {
        Iterator itP = proposals.entrySet().iterator();
        List<String> proposals = new ArrayList<String>();
        while (itP.hasNext()) {
            Map.Entry pair = (Map.Entry) itP.next();
            String playerName = Bukkit.getPlayer(UUID.fromString(pair.getValue().toString())).getDisplayName();
            String msg = pair.getKey().toString();
            proposals.add("Proposal from: " + playerName + " Proposal: " + msg);
        }
        return proposals;
    }

    public double getTrustworthy () {
        return this.trustworthy;
    }

    public double getLoyalty () {
        return this.loyalty;
    }

    public double getAggression () {
        return this.aggression;
    }

    public void addTrustworthy (float value) {
        this.trustworthy += value;
    }

    public void addLoyalty (float value) {
        this.loyalty += value;
    }

    public void addAggression (float value) {
        this.aggression += value;
    }

    public String getFactionName () {
        return factionName;
    }

    public boolean getFactionAtWar (String name) {
        if (factionsAtWar.isEmpty()) return false;
        for (String f : factionsAtWar) {
            if (name.equalsIgnoreCase(f)) return true;
        }
        return false;
    }

    public void addFactionAtWar (String name) {
        factionsAtWar.add(name);
    }

    public List<String> getFactionsAtWar () {
        return factionsAtWar;
    }

    public void stopWar (String name) {
        String del = "";
        for (String f : factionsAtWar) {
            if (f.equalsIgnoreCase(name)) {
                del = f;
                break;
            }
        }
        if (del.equals("")) {
            return;
        }
        factionsAtWar.remove(del);
    }

    public boolean getFactionNAP (String name) {
        if (factionsNAP.isEmpty()) return false;
        for (String f : factionsNAP) {
            if (name.equalsIgnoreCase(f)) return true;
        }
        return false;
    }

    public void addFactionNAP (String name) {
        factionsNAP.add(name);
    }

    public List<String> getFactionsNAP () {
        return factionsNAP;
    }

    HashMap<String, UUID> requests = new HashMap<String, UUID>();
    HashMap<String, UUID> proposals = new HashMap<String, UUID>();
    List<String> factionsAtWar = new ArrayList<String>();
    List<String> factionsNAP = new ArrayList<String>();
    List<Contract> factionTrades = new ArrayList<>();
    public String factionName;
    double trustworthy = 0;
    double loyalty = 0;
    double aggression = 0;
}
