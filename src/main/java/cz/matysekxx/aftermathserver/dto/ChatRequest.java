package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/// DTO for incoming chat messages from the client.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    /// The content of the chat message.
    private String message;
}