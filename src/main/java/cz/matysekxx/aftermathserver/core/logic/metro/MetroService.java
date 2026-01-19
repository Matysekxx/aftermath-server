package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MetroService {

    private Map<String, List<MetroStation>> metroStations;
    @PostConstruct
    private void init() {
        //TODO: pridat nacitani stanic metra z resources
    }

    public void handleStationTrigger(Player player, String stationId) {
        //TODO: po plnem pridani trigger logiky v GameEngine implementovat cestovani  mezi stanicami diky triggerum
    }

    public void processTravel(Player player, String targetStationId) {

    }

    public List<MetroStation> getAvailableDestinations(Player player) {
        //TODO: podle linky na  ktere se hrac vyskytuje se mu poslou mozne destinace
        return null;
    }
}
