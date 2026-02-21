package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DialogResponse {
    private String npcName;
    private String text;

    public static DialogResponse of(String npcName, String text) {
        return new DialogResponse(npcName, text);
    }
}
