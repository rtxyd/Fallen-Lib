package net.rtxyd.fallen.lib.runtime.forgemod.addon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown.CooldownHandler;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown.ICooldownHandler;

public class Common {

    public static void bootstrap(IEventBus bus) {
        DeferredRegisters.bootstrap(bus);
        AttachmentTypes.bootstrap();
    }

    public static class DeferredRegisters {
        public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FallenLib.MODID);
        public static void bootstrap(IEventBus bus) {
            bus.register(ATTACHMENT_TYPES);
        }
    }

    public static class AttachmentTypes {
        public static final AttachmentType<ICooldownHandler> CD_HANDLER =
                AttachmentType.<ICooldownHandler>builder(CooldownHandler::new).build();
        static {
            DeferredRegisters.ATTACHMENT_TYPES.register("cd_handler", () -> CD_HANDLER);
        }
        public static void bootstrap() {};
    }
}
