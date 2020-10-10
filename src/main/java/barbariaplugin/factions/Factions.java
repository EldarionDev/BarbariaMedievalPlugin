package barbariaplugin.factions;

import java.util.HashMap;
import java.util.UUID;

public class Factions {
    public Factions() {

    }

    static Faction getFaction(String name) {
        return factions.get(name);
    }

    static void addFaction(String name, UUID factionLeaderUUID) {
        factions.put(name, new Faction());
        factionLeaders.put(name, factionLeaderUUID);
    }

    static UUID getFactionLeader(String name) {
        return factionLeaders.get(name);
    }

    static void setFactionLeader(String name, UUID factionLeaderUUID) {
        factionLeaders.replace(name, factionLeaderUUID);
    }

    static HashMap<String, Faction> factions = new HashMap<String, Faction>();
    static HashMap<String, UUID> factionLeaders = new HashMap<String, UUID>();
}
