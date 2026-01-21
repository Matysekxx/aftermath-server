package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import lombok.Data;

import java.util.List;

@Data
public class UILoadResponse {
    private List<MetroStation> stations;
    private String lineId;
}
