package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a player login request.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class LoginRequest {
    /**
     * The desired username.
     */
    private String username;
    /**
     * The selected player class.
     */
    private String playerClass;
    /**
     * The ID of the starting map.
     */
    private String startingMapId;
}
