package barbariaplugin.factions;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Factions {
    public Factions() {

    }

    public static void load () {
        File dir = new File("factions/");
        dir.mkdirs();
    }

    public static void save () {
        Iterator itF = factions.entrySet().iterator();
        while (itF.hasNext()) {
            Map.Entry pair = (Map.Entry) itF.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("faction_leader", factionLeaders.get(pair.getKey()));
            try {
                FileWriter jsonFile = new FileWriter("factions/" + pair.getKey() + ".json");
                jsonFile.write(jsonObject.toJSONString());
                jsonFile.close();
            }
            catch (IOException e) {

            }
        }
    }

    public static Faction getFaction(String name) {
        return factions.get(name);
    }

    public static void addFaction(String name, UUID factionLeaderUUID) {
        factions.put(name, new Faction());
        factionLeaders.put(name, factionLeaderUUID);
        factionMembers.put(factionLeaderUUID, name);
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
        if ((playerUUID.compareTo(factionLeaders.get(name))) == 0) {
            return true;
        }
        return false;
    }

    public static UUID getFactionLeader(String name) {
        return factionLeaders.get(name);
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
        factionLeaders.replace(name, factionLeaderUUID);
    }

    static HashMap<String, Faction> factions = new HashMap<String, Faction>();
    static HashMap<String, UUID> factionLeaders = new HashMap<String, UUID>();
    static HashMap<UUID, String> factionMembers = new HashMap<UUID, String>();
}
