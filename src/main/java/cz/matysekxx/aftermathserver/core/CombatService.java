package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.QuadTree;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CombatService {
    private final GameEventQueue gameEventQueue;
    private final Map<String, QuadTree>  quadTrees = new ConcurrentHashMap<>();

    //TODO: implementovat QuadTree
    public CombatService(GameEventQueue gameEventQueue) {
        this.gameEventQueue = gameEventQueue;
    }

    @PostConstruct
    public void init() {

    }



}
