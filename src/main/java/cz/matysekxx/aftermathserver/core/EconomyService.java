package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import org.springframework.stereotype.Service;

@Service
public class EconomyService {

    public void processDailyDebt(Player player) {
        //TODO: implementovat logiku odebrani kreditu od hrace
    }

    public void applyPayment(Player player, double amount) {
        //TODO: implementovat logiku snizeni dluhu
    }
}
