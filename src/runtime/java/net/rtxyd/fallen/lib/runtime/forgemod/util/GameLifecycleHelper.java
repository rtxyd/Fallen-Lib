package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.SlotOnTakeEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.util.eventkey.EventKey;
import net.rtxyd.fallen.lib.runtime.forgemod.util.eventkey.EventKeys;
import net.rtxyd.fallen.lib.util.call.ContextKey;
import net.rtxyd.fallen.lib.util.call.ContextKeyRegistry;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@EventBusSubscriber
public class GameLifecycleHelper {
    public static IClientPlayerSupplier pSupplier = () -> null;
    static IClientThreadSupplier tSupplier = () -> null;
    private static int maxContainerMapSize = -1;
    static int serverCycleTickCount = -2;
    static int clientCycleTickCount = -2;

    private static final ContextKeyRegistry CTX_KEY_REG = new ContextKeyRegistry();
    static final ServerClientCallBox CALL_BOX = new ServerClientCallBox();
    private static final Map<Container, Set<Player>> CONTAINER_PLAYER_MAP = new HashMap<>();
    private static final Map<Player, AbstractContainerMenu> PLAYER_MENU_SNAPSHOT = new HashMap<>();

    public static final CallKey<AbstractContainerMenu> LAST_MENU = CTX_KEY_REG.register("fallen_lib.player.menu", CallKey::new);
    public static final Consumer<Exception> EMPTY_EX_CONSUMER = o -> {};

    public static <T> CallKey<T> registerCallKey(String id) {
        if (FallenLib.getStage() == FallenLib.Stage.COMPLETE) {
            throw new UnsupportedOperationException("ContextKey can only be registered on mod loading phase");
        }
        return CTX_KEY_REG.register(id, CallKey::new);
    }

    public static <T> CallKey<T> getCallKey(String id) {
        return CTX_KEY_REG.get(id);
    }

    public static <C, H> void submitCallback(EventKey<C, H> eventKey, C anchor, H handler) {
        eventKey.submit(anchor, handler);
    }

    public static Set<Player> getContainerActivePlayers(Container menu) {
        return CONTAINER_PLAYER_MAP.get(menu);
    }

    /* this is a backward compatible layer for version 1.4.4 and before */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static <T> ContextKey<T> registerContextKey(String id) {
        return registerCallKey(id);
    }
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static <T> ContextKey<T> getContextKey(String id) {
        return getCallKey(id);
    }
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static <T> void submitContextCall(ContextKey<T> key, Callable<T> call) {
        if (key instanceof CallKey<T> callKey) {
            submitContextCall(callKey, call);
        }
    }
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static <T> T callIfPresent(ContextKey<T> key, Consumer<Exception> handleEx) {
        if (key instanceof CallKey<T> callKey) {
            return callIfSameTick(callKey, handleEx);
        }
        return null;
    }
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static <T> T callAndRemoveIfPresent(ContextKey<T> key, Consumer<Exception> handleEx) {
        if (key instanceof CallKey<T> callKey) {
            return callAndRemoveIfSameTick(callKey, handleEx);
        }
        return null;
    }

    public static <T> void submitContextCall(CallKey<T> key, Callable<T> call) {
        key.init();
        CALL_BOX.submit(key, call);
    }

    public static <T> T callIfPresent(CallKey<T> key, Consumer<Exception> handleEx) {
        if (!key.isInitialized()) return null;
        key.updateCallCount();
        return CALL_BOX.getAndCallIfPresent(key, handleEx);
    }

    public static <T> T callAndRemoveIfPresent(CallKey<T> key, Consumer<Exception> handleEx) {
        key.resetCallCount();
        return CALL_BOX.takeAndCallIfPresent(key, handleEx);
    }

    public static <T> T callAndRemoveIfFirst(CallKey<T> key, Consumer<Exception> handleEx) {
        if (key.isCalled()) return null;
        key.resetCallCount();
        return CALL_BOX.takeAndCallIfPresent(key, handleEx);
    }

    public static <T> T callAndRemoveIfCalled(CallKey<T> key, Consumer<Exception> handleEx) {
        if (!key.isCalled()) return null;
        key.resetCallCount();
        return CALL_BOX.takeAndCallIfPresent(key, handleEx);
    }

    public static <T> T callIfSameTick(CallKey<T> key, Consumer<Exception> handleEx) {
        if (!key.isInitialized() || !isInSameTick(key)) return null;
        key.updateCallCount();
        return CALL_BOX.getAndCallIfPresent(key, handleEx);
    }

    public static <T> T callAndRemoveIfSameTick(CallKey<T> key, Consumer<Exception> handleEx) {
        if (!isInSameTick(key)) return null;
        key.resetCallCount();
        return CALL_BOX.takeAndCallIfPresent(key, handleEx);
    }

    public static <T> T callAndRemoveIfSameTickFirst(CallKey<T> key, Consumer<Exception> handleEx) {
        if (key.isCalled() || !isInSameTick(key)) return null;
        key.resetCallCount();
        return CALL_BOX.takeAndCallIfPresent(key, handleEx);
    }

    public static void removeWithoutCall(CallKey<?> key) {
        key.resetCallCount();
        CALL_BOX.remove(key);
    }

    public static int getClientCycleTickCount() {
        return clientCycleTickCount;
    }

    public static int getServerCycleTickCount() {
        return serverCycleTickCount;
    }

    public static boolean isClientSide() {
        return !CALL_BOX.isThread0();
    }

    public static boolean isInSameTick(CallKey<?> key) {
        if (key.getCallCount() > -1) {
            if (isClientSide()) {
                int client = key.getClientFingerprint();
                if (client > -1) {
                    return client == clientCycleTickCount;
                }
            } else {
                int server = key.getServerFingerprint();
                if (server > -1) {
                    return server == serverCycleTickCount;
                }
            }
        }
        return false;
    }

    public static Player getClientPlayer() {
        return pSupplier.player();
    }

    public static Optional<Level> safeGetClientLevel() {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            Player player = pSupplier.player();
            if (player != null) {
                return Optional.of(player.level());
            }
        }
        return Optional.empty();
    }

    public static Level getClientLevel() {
        Player localPlayer = pSupplier.player();
        if (localPlayer == null) return null;
        return localPlayer.level();
    }

    @SubscribeEvent
    static void onServerStart(ServerStartedEvent event) {
        CALL_BOX.setThread(0, Thread.currentThread());
        serverCycleTickCount = 0;
    }
    @SubscribeEvent
    static void onServerTickStart(ServerTickEvent.Pre e) {
        ++serverCycleTickCount;
    }
    @SubscribeEvent
    static void onClientTickStart(ClientTickEvent.Pre e) {
        clientCycleTickCount++;
    }
    @SubscribeEvent
    static void onServerStopped(ServerStoppedEvent e) {
        CALL_BOX.clear();
        EventKeys.clearAll();
        CONTAINER_PLAYER_MAP.clear();
        PLAYER_MENU_SNAPSHOT.clear();
        CTX_KEY_REG.forEachContextKey(c -> {
            if (c instanceof CallKey<?> callKey) {
                callKey.resetCallCount();
            }
        });
        serverCycleTickCount = 0;
    }
    @SubscribeEvent
    static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent e) {
        removePlayerFromAllContainers(e.getEntity());
        PLAYER_MENU_SNAPSHOT.remove(e.getEntity());
    }
    static void removePlayerFromAllContainers(Player p) {
        for (Set<Player> set : CONTAINER_PLAYER_MAP.values()) {
            set.remove(p);
        }
        CONTAINER_PLAYER_MAP.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
    @SubscribeEvent
    static void onSlotTake(SlotOnTakeEvent e) {
        EventKeys.SLOT_ON_TAKE.fire(e.getSlot(), e.getPlayer(), e.getStack());
    }
    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post e) {
        Player p = e.getEntity();
        if (p.level().isClientSide) return;
//        if (p.tickCount % 3 != 0) return;
        AbstractContainerMenu menu = PLAYER_MENU_SNAPSHOT.get(p);
        if (menu == null) return;
        if (p.containerMenu == menu) {
            return;
        } else {
            if (p.containerMenu == p.inventoryMenu) {
                updateContainerMap(menu, p);
            } else {
                if (p.containerMenu != null && p.containerMenu.stillValid(p)) {
                    PLAYER_MENU_SNAPSHOT.put(p, p.containerMenu);
                    storeContainerMap(p);
                    updateContainerMap(menu, p);
                }
            }
        }
    }
    @SubscribeEvent
    static void onContainerOpen(PlayerContainerEvent.Open e) {
        Player p = e.getEntity();
        if (p.level().isClientSide) return;
        AbstractContainerMenu menu = e.getContainer();
        PLAYER_MENU_SNAPSHOT.put(p, menu);
        if (menu != p.inventoryMenu) {
            storeContainerMap(p);
            int size = CONTAINER_PLAYER_MAP.size();
//            if (size > 400) {
//                FallenLib.LOGGER.warn("Container map unusually large: {}", size);
//            }
            if (size > maxContainerMapSize) {
                maxContainerMapSize = size;
                FallenLib.LOGGER.info("New max container map size: {}", size);
            }
        }
    }
    @SubscribeEvent
    static void onContainerClose(PlayerContainerEvent.Close e) {
        Player p = e.getEntity();
        if (p.level().isClientSide) return;
        AbstractContainerMenu menu = e.getContainer();
        PLAYER_MENU_SNAPSHOT.remove(p);
        if (menu != p.inventoryMenu) {
            updateContainerMap(e.getContainer(), e.getEntity());
        }
    }
    private static void storeContainerMap(Player p) {
        if (p.containerMenu == null) return;
        Container last = null;
        for (Slot slot : p.containerMenu.slots) {
            if (last != slot.container) {
                last = slot.container;
                CONTAINER_PLAYER_MAP.computeIfAbsent(last, c -> new HashSet<>()).add(p);
            }
        }
    }
    private static void updateContainerMap(AbstractContainerMenu menu, Player p) {
        if (p.containerMenu == null || menu == null) return;
        Container last = null;
        for (Slot slot : menu.slots) {
            if (last != slot.container) {
                last = slot.container;
                Set<Player> pSet = CONTAINER_PLAYER_MAP.get(last);
                if (pSet == null) continue;
                pSet.remove(p);
                if (pSet.isEmpty()) {
                    CONTAINER_PLAYER_MAP.remove(last);
                    EventKeys.SLOT_ON_TAKE.cleanup(last);
                }
            }
        }
    }
}
