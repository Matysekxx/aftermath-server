package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.ItemFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.dto.BuyRequest;
import cz.matysekxx.aftermathserver.dto.SellRequest;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for managing the game's economic systems.
 * <p>
 * This includes credit transactions, debt accumulation, interest rates,
 * and price calculations for buying and selling items.
 *
 * @author Matysekxx
 */
@Service
@Slf4j
public class EconomyService {

    private final GameEventQueue gameEventQueue;
    private final ItemFactory itemFactory;
    private final GlobalState globalState;

    public EconomyService(GameEventQueue gameEventQueue, ItemFactory itemFactory, GlobalState globalState) {
        this.gameEventQueue = gameEventQueue;
        this.itemFactory = itemFactory;
        this.globalState = globalState;
    }

    /**
     * Processes a buy request from a player.
     * <p>
     * Validates the trader, distance, funds, and inventory space before executing the transaction.
     *
     * @param player  The player making the purchase.
     * @param npc     The NPC trader.
     * @param request The buy request details.
     */
    public void processBuy(Player player, Npc npc, BuyRequest request) {
        if (MathUtil.getChebyshevDistance(player, npc) > 2) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Too far away from trader", player.getId()));
            return;
        }

        final List<Item> shopItems = npc.getShopItems();
        if (shopItems == null || request.getItemIndex() < 0 || request.getItemIndex() >= shopItems.size()) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Invalid item selection", player.getId()));
            return;
        }
        final Item itemToBuy = shopItems.get(request.getItemIndex());

        final int price = itemToBuy.getPrice() != null ? itemToBuy.getPrice() : 0;
        if (price <= 0) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Item has no price", player.getId()));
            return;
        }

        if (!canAfford(player, price)) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Not enough credits", player.getId()));
            return;
        }
        final Item newItem = itemFactory.createItem(itemToBuy.getId(), 1);
        if (player.getInventory().addItem(newItem)) {
            removeCredits(player, price);
            gameEventQueue.enqueue(GameEventFactory.sendInventoryEvent(player));
            gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
            gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("Bought " + newItem.getName(), player.getId()));
        } else gameEventQueue.enqueue(
                GameEventFactory.sendErrorEvent("Inventory full", player.getId()));

    }

    /**
     * Processes a sell request from a player.
     *
     * @param player  The player selling the item.
     * @param npc     The NPC trader.
     * @param request The sell request details.
     */
    public void processSell(Player player, Npc npc, SellRequest request) {
        if (MathUtil.getChebyshevDistance(player, npc) > 2) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Too far away from trader", player.getId()));
            return;
        }

        final Optional<Item> itemOpt = player.getInventory().removeItem(request.getSlotIndex(), 1);
        if (itemOpt.isEmpty()) {
            gameEventQueue.enqueue(
                    GameEventFactory.sendErrorEvent("Item not found", player.getId()));
            return;
        }

        final Item item = itemOpt.get();
        final int sellPrice = calculateSellPrice(item, player);

        addCredits(player, sellPrice);
        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("Sold " + item.getName() + " for " + sellPrice + " CR", player.getId()));

        log.info("Player {} sold {} for {} CR. New balance: {}", player.getName(), item.getName(), sellPrice, player.getCredits());

        gameEventQueue.enqueue(GameEventFactory.sendInventoryEvent(player));
        gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
    }

    /**
     * Allows the player to pay off a specific amount of their personal debt.
     *
     * @param player The player making the payment.
     * @param amount The amount to pay.
     */
    public void payPersonalDebt(Player player, int amount) {
        if (amount <= 0) return;
        if (player.getDebt() <= 0) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You have no debt to pay!", player.getId()));
            return;
        }
        if (!canAfford(player, amount)) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Not enough credits.", player.getId()));
            return;
        }

        final int actualPayment = Math.min(amount, player.getDebt());
        removeCredits(player, actualPayment);
        player.setDebt(player.getDebt() - actualPayment);

        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("Paid " + actualPayment + " CR towards personal debt.", player.getId()));
        gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
    }

    /**
     * Contributes to the global debt if the player has no personal debt.
     *
     * @param player The player contributing.
     * @param amount The amount to contribute.
     */
    public void contributeToGlobalDebt(Player player, int amount) {
        if (player.getDebt() > 0) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You must pay your personal debt first!", player.getId()));
            return;
        }
        if (!canAfford(player, amount)) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Not enough credits.", player.getId()));
            return;
        }

        removeCredits(player, amount);
        final long remainingGlobalDebt = globalState.payGlobalDebt(amount);

        gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
        gameEventQueue.enqueue(GameEventFactory.sendGlobalAnnouncementEvent(
                "Player " + player.getName() + " contributed " + amount + " CR to the Global Debt. Remaining: " + remainingGlobalDebt));

        if (remainingGlobalDebt <= 0) {
            gameEventQueue.enqueue(GameEventFactory.sendGlobalAnnouncementEvent("THE GLOBAL DEBT HAS BEEN PAID!"));
        }
    }

    /**
     * Processes the end-of-day debt cycle.
     * <p>
     * Combines the base living fee with any accumulated activity costs (like travel)
     * and applies them to the player's total debt.
     *
     * @param player The player to process.
     */
    public void processDailyDebt(Player player) {
        final int baseFee = 20;
        final int totalDailyCost = baseFee + player.getPendingCosts();
        player.setDebt(player.getDebt() + totalDailyCost);
        player.setPendingCosts(0);
    }

    /**
     * Records a specific cost incurred by player activity during the day.
     * <p>
     * This could be for metro travel, entering specific zones, or service fees.
     * These costs are usually added to the daily bill instead of being paid instantly.
     *
     * @param player The player who performed the activity.
     * @param amount The cost of the activity.
     */
    public void recordActivityCost(Player player, int amount) {
        player.setPendingCosts(player.getPendingCosts() + amount);
    }

    /**
     * Deducts credits from a player's balance to pay off a portion of their debt.
     *
     * @param player The player making the payment.
     * @param amount The amount of credits to transfer from balance to debt reduction.
     * @return true if the payment was successful, false if the player has insufficient credits.
     */
    public boolean applyPayment(Player player, int amount) {
        if (canAfford(player, amount)) {
            player.removeCredits(amount);
            player.setDebt(Math.max(0, player.getDebt() - amount));
            return true;
        }
        return false;
    }

    /**
     * Checks if a player has enough credits to afford a specific cost.
     *
     * @param player The player to check.
     * @param cost   The required amount of credits.
     * @return true if the player's credits are greater than or equal to the cost.
     */
    public boolean canAfford(Player player, int cost) {
        return player.getCredits() >= cost;
    }

    /**
     * Adds credits to a player's balance.
     * <p>
     * Used for rewards, selling items, or finding loot.
     *
     * @param player The recipient player.
     * @param amount The amount of credits to add.
     */
    public void addCredits(Player player, int amount) {
        player.addCredits(amount);
    }

    /**
     * Deducts credits from a player's balance for a purchase or fee.
     *
     * @param player The player paying.
     * @param amount The amount of credits to remove.
     * @return true if the transaction was successful.
     */
    public boolean removeCredits(Player player, int amount) {
        if (canAfford(player, amount)) {
            player.removeCredits(amount);
            return true;
        }
        return false;
    }

    /**
     * Calculates the final price of an item when sold by a player to an NPC.
     * <p>
     * This may eventually take into account player skills, reputation, or item condition.
     *
     * @param item   The item being sold.
     * @param player The player selling the item.
     * @return The calculated credit value.
     */
    public int calculateSellPrice(Item item, Player player) {
        int basePrice = item.getPrice() != null ? item.getPrice() : 0;
        return Math.max(1, (int) (basePrice * 0.5));
    }
}