package cz.matysekxx.aftermathserver.event;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameEventQueue {
    private final ConcurrentLinkedQueue<GameEvent> queue = new ConcurrentLinkedQueue<>();

    public GameEvent poll() {
        return queue.poll();
    }

    public void enqueue(GameEvent gameEvent) {
        queue.add(gameEvent);
    }
}
