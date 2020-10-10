package barbariaplugin.factions;

import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Faction {
    public Faction() {

    }

    public void load () {

    }

    public void save (JSONObject object) {
        JSONObject returnObject;
        JSONArray requests_array = new JSONArray();
        Iterator itR = requests.entrySet().iterator();
        while (itR.hasNext()) {
            Map.Entry pair = (Map.Entry) itR.next();
            requests_array.add(pair.getValue().toString());
        }
        returnObject = object;
        returnObject.put("requests", requests_array);
    }

    public void addRequest (Player player) {
        requests.put(player.getDisplayName(), player.getUniqueId());
    }

    static HashMap<String, UUID> requests = new HashMap<String, UUID>();
}
