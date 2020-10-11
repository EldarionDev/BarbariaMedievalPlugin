package barbariaplugin.factions;

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

    private void load () {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("factions/" + factionName + ".json"));
            JSONArray jsonRequests = (JSONArray) jsonObject.get("requests");
            JSONArray jsonProposers = (JSONArray) jsonObject.get("proposers");
            JSONArray jsonProposals = (JSONArray) jsonObject.get("proposals");
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
        returnObject = object;
        returnObject.put("requests", requests_array);
        returnObject.put("proposers", proposer_array);
        returnObject.put("proposals", proposals_array);
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
            proposals.add("Proposal from: " + playerName + "Proposal: " + msg);
        }
        return proposals;
    }

    HashMap<String, UUID> requests = new HashMap<String, UUID>();
    HashMap<String, UUID> proposals = new HashMap<String, UUID>();
    public String factionName;
}
