package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/// DTO for player movement requests.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveRequest {
    /// The direction of movement (UP, DOWN, LEFT, RIGHT).
    private String direction;
}