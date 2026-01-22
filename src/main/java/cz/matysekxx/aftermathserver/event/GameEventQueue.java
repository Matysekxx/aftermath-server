package cz.matysekxx.aftermathserver.event;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;

/// A thread-safe queue for buffering game events.
///
/// Acts as a bridge between the game logic thread and the network/event processing loop.
@Service
public class GameEventQueue {
    private final LinkedBlockingDeque<GameEvent> queue = new LinkedBlockingDeque<>();

    /// Adds an event to the queue.
    public void enqueue(GameEvent gameEvent) {
        queue.add(gameEvent);
    }

    /// Retrieves and removes the head of the queue, waiting if necessary.
    public GameEvent take() throws InterruptedException {
        return queue.take();
    }
}
