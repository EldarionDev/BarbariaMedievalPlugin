package barbariaplugin.factions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Factions {
    public Factions() {

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

    public static boolean checkFactionJoin(UUID memberUUID) {
        Iterator itM = factionMembers.entrySet().iterator();
        while (itM.hasNext()) {
            Map.Entry pair = (Map.Entry)itM.next();
            if (pair.getKey().equals(memberUUID)) {
                return false;
            }
        }
        return true;
    }

    public static UUID getFactionLeader(String name) {
        return factionLeaders.get(name);
    }

    public static void setFactionLeader(String name, UUID factionLeaderUUID) {
        factionLeaders.replace(name, factionLeaderUUID);
    }

    static HashMap<String, Faction> factions = new HashMap<String, Faction>();
    static HashMap<String, UUID> factionLeaders = new HashMap<String, UUID>();
    static HashMap<UUID, String> factionMembers = new HashMap<UUID, String>();
}
