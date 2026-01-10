package cz.matysekxx.aftermathserver.event;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;

@Service
public class GameEventQueue {
    private final LinkedBlockingDeque<GameEvent> queue = new LinkedBlockingDeque<>();

    public void enqueue(GameEvent gameEvent) {
        queue.add(gameEvent);
    }

    public GameEvent take() throws InterruptedException {
        return queue.take();
    }
}
