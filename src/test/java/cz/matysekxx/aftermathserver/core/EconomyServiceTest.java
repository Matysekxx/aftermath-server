package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.factory.ItemFactory;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.dto.BuyRequest;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EconomyServiceTest {

    private EconomyService economyService;
    private FakeItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        final GameEventQueue gameEventQueue = new GameEventQueue();
        itemFactory = new FakeItemFactory();
        economyService = new EconomyService(gameEventQueue, itemFactory, new GlobalState(1000000));
    }

    @Test
    void testProcessBuySuccess() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player(
                "p1", "Player", Vector3.of(0, 0, 0),
                config, "map1", "SOLDIER");
        player.setCredits(100);

        final Npc npc = new Npc(
                "n1", "Trader", 1, 1, 0,
                "map1", 100, null, InteractionType.TRADE);
        final Item shopItem = Item.builder()
                .id("medkit")
                .name("Medkit")
                .price(40)
                .quantity(1)
                .weight(0.5)
                .maxStack(5)
                .type(ItemType.CONSUMABLE)
                .build();
        npc.setShopItems(List.of(shopItem));

        itemFactory.setItemToReturn(shopItem.cloneWithQuantity(1));

        final BuyRequest request = new BuyRequest();
        request.setNpcId("n1");
        request.setItemIndex(0);

        economyService.processBuy(player, npc, request);

        assertEquals(60, player.getCredits());
        assertEquals(1, player.getInventory().getSlots().size());
        assertEquals("medkit", player.getInventory().getSlots().get(0).getId());
    }

    @Test
    void testProcessBuyInsufficientFunds() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player("p1", "Player", Vector3.of(0, 0, 0), config, "map1", "SOLDIER");
        player.setCredits(10);

        final Npc npc = new Npc(
                "n1", "Trader", 1, 1, 0,
                "map1", 100, null, InteractionType.TRADE);
        final Item shopItem = Item.builder().id("medkit").price(40).build();
        npc.setShopItems(List.of(shopItem));

        final BuyRequest request = new BuyRequest();
        request.setNpcId("n1");
        request.setItemIndex(0);

        economyService.processBuy(player, npc, request);

        assertEquals(10, player.getCredits());
        assertTrue(player.getInventory().getSlots().isEmpty());
    }

    @Test
    void testProcessBuyTooFar() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player(
                "p1", "Player", Vector3.of(0, 0, 0),
                config, "map1", "SOLDIER");
        player.setCredits(100);

        final Npc npc = new Npc(
                "n1", "Trader", 10, 10, 0,
                "map1", 100, null, InteractionType.TRADE);
        final Item shopItem = Item.builder().id("medkit").price(40).build();
        npc.setShopItems(List.of(shopItem));

        final BuyRequest request = new BuyRequest();
        request.setNpcId("n1");
        request.setItemIndex(0);

        economyService.processBuy(player, npc, request);

        assertEquals(100, player.getCredits());
        assertTrue(player.getInventory().getSlots().isEmpty());
    }

    @Test
    void testDailyDebtAccumulation() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player(
                "p1", "Player", Vector3.of(0, 0, 0), config,
                "map1", "SOLDIER");
        player.setDebt(100);
        player.setPendingCosts(50);

        economyService.processDailyDebt(player);
        assertEquals(170, player.getDebt());
        assertEquals(0, player.getPendingCosts());
    }

    private static class FakeItemFactory extends ItemFactory {
        private Item itemToReturn;
        public FakeItemFactory() { super(null); }
        public void setItemToReturn(Item item) { this.itemToReturn = item; }
        @Override public Item createItem(String id, int quantity) {
            if (itemToReturn != null) return itemToReturn;
            return Item.builder().id(id).quantity(quantity).build();
        }
    }
}
