package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import org.springframework.stereotype.Service;

/// Service managing game economy.
///
/// Handles debts and payments.
@Service
public class EconomyService {

    /// Processes daily debt accumulation for a player.
    public void processDailyDebt(Player player) {
        //TODO: implementovat logiku odebrani kreditu od hrace
    }

    /// Applies a payment to reduce player's debt.
    public void applyPayment(Player player, double amount) {
        //TODO: implementovat logiku snizeni dluhu
    }
}
