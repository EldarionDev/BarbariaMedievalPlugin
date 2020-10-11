package barbariaplugin.factions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class Factions {
    public Factions() {

    }

    public static void load () {
        File dir = new File("factions/");
        dir.mkdirs();
        File filesList[] = dir.listFiles();
        for (File currentFile : filesList) {
            String sepEnding[] = currentFile.getName().split("\\.");
            String factionName = sepEnding[0];
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(currentFile.getAbsolutePath().toString()));
                String jsonLeader = (String) jsonObject.get("faction_leader");
                JSONArray jsonMembers = (JSONArray) jsonObject.get("faction_members");
                factionLeaders.put(factionName, UUID.fromString(jsonLeader));
                factions.put(factionName, new Faction(factionName));
                Iterator iterator  = jsonMembers.iterator();
                while (iterator.hasNext()) {
                    factionMembers.put(UUID.fromString(iterator.next().toString()), factionName);
                }
            }
            catch (ParseException e) {

            }
            catch (FileNotFoundException e) {

            }
            catch (IOException e) {

            }
        }
    }

    public static void save () {
        Iterator itF = factions.entrySet().iterator();
        while (itF.hasNext()) {
            Map.Entry pair = (Map.Entry) itF.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("faction_leader", factionLeaders.get(pair.getKey().toString()).toString());
            JSONArray members = new JSONArray();
            Iterator itM = factionMembers.entrySet().iterator();
            while (itM.hasNext()) {
                Map.Entry member_pair = (Map.Entry) itM.next();
                if (member_pair.getValue().toString().equalsIgnoreCase(pair.getKey().toString())) {
                    members.add(member_pair.getKey().toString());
                }
            }
            jsonObject.put("faction_members", members);
            factions.get(pair.getKey().toString()).save(jsonObject);
            try {
                FileWriter jsonFile = new FileWriter("factions/" + pair.getKey() + ".json");
                jsonFile.write(jsonObject.toJSONString());
                jsonFile.close();
            }
            catch (IOException e) {

            }
        }
    }

    public static boolean checkFriendlyFire(Player attacker, Player defender) {
        if (getMemberFactionName(attacker.getUniqueId()).equalsIgnoreCase(getMemberFactionName(defender.getUniqueId()))) {
            if (getMemberFactionName(attacker.getUniqueId()).equalsIgnoreCase("")) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static Faction getFaction(String name) {
        return factions.get(name);
    }

    public static void addFaction(String name, UUID factionLeaderUUID) {
        factions.put(name, new Faction(name));
        factionLeaders.put(name, factionLeaderUUID);
        factionMembers.put(factionLeaderUUID, name);
    }

    public static void addMember(String name, UUID member) {
        factionMembers.put(member, name);
        Bukkit.getPlayer(member).sendMessage("You have been added successfully to: " + name);
    }
    
    public static List<String> getFactions() {
        List<String> faction_names = new ArrayList<String>();
        Iterator itF = factions.entrySet().iterator();
        int i = 0;
        while (itF.hasNext()) {
            Map.Entry pair = (Map.Entry) itF.next();
            faction_names.add(pair.getKey().toString());
        }
        return faction_names;
    }

    public static boolean checkFactionCreate(String name, UUID factionLeaderUUID) {
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry)itM.next();
            if (pair.getKey().equals(factionLeaderUUID)) {
                return false;
            }
        }
        Iterator itF = factions.entrySet().iterator();
        while (itF.hasNext()) {
            Map.Entry pair = (Map.Entry)itF.next();
            if (pair.getKey().toString().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPlayerInFaction(UUID memberUUID) {
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry)itM.next();
            if (pair.getKey().equals(memberUUID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPlayerFactionLeader(UUID playerUUID, String name) {
        if (playerUUID == null || factionLeaders == null) {
            return false;
        }
        if ((playerUUID.compareTo(factionLeaders.get(name))) == 0) {
            return true;
        }
        return false;
    }

    public static UUID getFactionLeader(String name) {
        return factionLeaders.get(name);
    }

    public static List<String> getMembers(String name) {
        List<String> returnList = new ArrayList<String>();
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry) itM.next();
            if (pair.getValue().toString().equalsIgnoreCase(name)) {
                returnList.add(Bukkit.getPlayer((UUID) pair.getKey()).getDisplayName());
            }
        }
        return returnList;
    }

    public static String getMemberFactionName(UUID member) {
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry)itM.next();
            if (pair.getKey().equals(member)) {
                return pair.getValue().toString();
            }
        }
        return "";
    }

    public static void setFactionLeader(String name, UUID factionLeaderUUID) {
        Iterator itL = factionLeaders.entrySet().iterator();
        while (itL.hasNext()) {
            Map.Entry pair = (Map.Entry) itL.next();
            if (pair.getKey().toString().equalsIgnoreCase(name)) {
                factionLeaders.replace(pair.getKey().toString(), factionLeaderUUID);
                Bukkit.getPlayer(factionLeaderUUID).sendMessage("You are now faction leader of: " + name);
            }
        }
    }

    public static void deleteFaction (String name, UUID leader) {
        factions.remove(name);
        factionLeaders.remove(name);
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry) itM.next();
            if (pair.getValue().toString().equalsIgnoreCase(name)) {
                factionMembers.remove(pair.getKey());
                Bukkit.getServer().broadcastMessage("Faction: " + name + " got deleted.");
                File factionFile = new File("factions/" + name + ".json");
                factionFile.delete();
                return;
            }
        }
        Bukkit.getPlayer(leader).sendMessage("Could not remove you from: " + name);
    }

    public static void removeMember (UUID member) {
        String factionName = new String("");
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair  = (Map.Entry) itM.next();
            if (pair.getKey().toString().equalsIgnoreCase(member.toString())) {
                factionMembers.remove(pair.getKey());
                factionName = pair.getValue().toString();
                Bukkit.getPlayer(member).sendMessage("Successfully removed you from: " + factionName);
                return;
            }
        }
        Bukkit.getPlayer(member).sendMessage("Could not remove you from: " + factionName);
    }

    static HashMap<String, Faction> factions = new HashMap<String, Faction>();
    static HashMap<String, UUID> factionLeaders = new HashMap<String, UUID>();
    static HashMap<UUID, String> factionMembers = new HashMap<UUID, String>();
}
