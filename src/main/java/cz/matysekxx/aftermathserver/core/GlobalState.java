package cz.matysekxx.aftermathserver.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds the global state of the game world, shared by all players.
 *
 * @author Matysekxx
 */
@Component
public class GlobalState {
    /**
     * The total debt of the metro community that needs to be repaid.
     */
    private final AtomicLong globalDebt;

    public GlobalState(@Value("${game.global-debt}") long initialDebt) {
        this.globalDebt = new AtomicLong(initialDebt);
    }

    /**
     * Gets the current global debt.
     *
     * @return The remaining global debt.
     */
    public long getGlobalDebt() {
        return globalDebt.get();
    }

    /**
     * Contributes to paying off the global debt.
     *
     * @param amount The amount to pay.
     * @return The new remaining debt.
     */
    public long payGlobalDebt(long amount) {
        long current = globalDebt.addAndGet(-amount);
        if (current < 0) {
            globalDebt.set(0);
            return 0;
        }
        return current;
    }
}