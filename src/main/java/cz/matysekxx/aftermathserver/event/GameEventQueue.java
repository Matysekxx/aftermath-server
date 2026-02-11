package cz.matysekxx.aftermathserver.event;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * A thread-safe queue for buffering game events.
 * <p>
 * Acts as a bridge between the game logic thread and the network/event processing loop.
 *
 * @author Matysekxx
 */
@Service
public class GameEventQueue {
    private final LinkedBlockingDeque<GameEvent> queue = new LinkedBlockingDeque<>();

    /**
     * Adds an event to the queue.
     * @param gameEvent The event to enqueue.
     */
    public void enqueue(GameEvent gameEvent) {
        queue.add(gameEvent);
    }

    /**
     * Retrieves and removes the head of the queue, waiting if necessary.
     * @return The next game event.
     * @throws InterruptedException if interrupted while waiting.
     */
    public GameEvent take() throws InterruptedException {
        return queue.take();
    }
}
