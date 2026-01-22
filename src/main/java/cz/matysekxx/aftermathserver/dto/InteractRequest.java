package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/// DTO for interaction requests.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InteractRequest {
    /// The type of interaction (optional/reserved for future use).
    private String type;
    /// The ID of the target object to interact with.
    private String target;
}