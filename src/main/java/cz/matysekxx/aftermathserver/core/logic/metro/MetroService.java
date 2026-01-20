package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class MetroService {
    private final Map<String, List<MetroStation>> metroStations;

    public MetroService(Map<String, List<MetroStation>> metroStations) {
        this.metroStations = metroStations;
    }

    public void handleStationTrigger(Player player, String lineId) {
    }

    public void processTravel(Player player, String targetStationId) {

    }

    public List<MetroStation> getAvailableDestinations(String lineId) {
        return metroStations.get(lineId);
    }
}
