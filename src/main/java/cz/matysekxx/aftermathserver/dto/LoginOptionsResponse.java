package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO containing available choices for character creation.
 *
 * @author Matysekxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginOptionsResponse {
    private List<String> classes;
    private List<SpawnPointInfo> maps;
}