package net.rtxyd.fallen.lib.runtime.forgemod.util.eventkey;

public final class EventKeys {
    public static final SlotOnTakeKey SLOT_ON_TAKE = new SlotOnTakeKey();

    public static void clearAll() {
        SLOT_ON_TAKE.clear();
    }
}
