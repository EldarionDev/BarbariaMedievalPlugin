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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
            Iterator iterator  = jsonRequests.iterator();
            while (iterator.hasNext()) {
                String myVar = iterator.next().toString();
                requests.put(Bukkit.getPlayer(UUID.fromString(myVar)).getDisplayName(), UUID.fromString(myVar));
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

    HashMap<String, UUID> requests = new HashMap<String, UUID>();
    String factionName;
}