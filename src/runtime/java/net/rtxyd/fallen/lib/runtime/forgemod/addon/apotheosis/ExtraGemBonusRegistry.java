package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis;

import com.google.common.base.Predicates;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.network.AbstractPacketBoundRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.network.ClientBoundSyncExtraGemBonusesPacket;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.SupplierCodec;

import java.util.List;
import java.util.function.Supplier;

public class ExtraGemBonusRegistry extends AbstractPacketBoundRegistry<ExtraGemBonusRegistry.ExtraGemBonus, ClientBoundSyncExtraGemBonusesPacket.Begin, ClientBoundSyncExtraGemBonusesPacket, ClientBoundSyncExtraGemBonusesPacket.End> {

    public static final ExtraGemBonusRegistry INSTANCE = new ExtraGemBonusRegistry();

    protected Multimap<DynamicHolder<Gem>, ExtraGemBonus> extraBonuses = HashMultimap.create();

    public ExtraGemBonusRegistry() {
        super(FallenLib.LOGGER, "extra_gem_bonuses", "type", Predicates.alwaysTrue(), true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerCodec(ResourceLocation.fromNamespaceAndPath(FallenLib.MODID, "extra_gem_bonus"), ExtraGemBonus.CODEC);

        // backward compatibility for lib version 1.3.2
        if (ModList.get().isLoaded("fallen_gems_affixes")) {
            this.registerCodec(ResourceLocation.fromNamespaceAndPath("fallen_gems_affixes", "extra_gem_bonus"), Codec.of(ExtraGemBonus.CODEC, ExtraGemBonus.CODEC));
        }
        //end
    }

    @Override
    public void beginReload() {
        this.registry = HashBiMap.create();
        this.extraBonuses = HashMultimap.create();
        this.clearExtraGemBonuses();
    }

    @Override
    public void onReload() {
        FallenLib.LOGGER.info("Loading extra gem bonus...");
        for (ExtraGemBonus extraBonus : registry.values()) {
            this.extraBonuses.put(extraBonus.gem, extraBonus);
        }
        FallenLib.LOGGER.info("Finalize loading...");
        this.applyExtraGemBonuses();
        FallenLib.LOGGER.info("Loading complete with {} entries", extraBonuses.size());
    }

    private void applyExtraGemBonuses() {
        FallenLib.LOGGER.info("Current GemRegistry size [{}]", GemRegistry.INSTANCE.getKeys().size());
        if (GemRegistry.INSTANCE.getKeys().isEmpty()) {
            FallenLib.LOGGER.error("GemRegistry is empty, this may be a loading priority issue.");
        }
        for (Gem gem : GemRegistry.INSTANCE.getValues()) {
            DynamicHolder<Gem> holder = GemRegistry.INSTANCE.holder(gem);

            for (ExtraGemBonus extraBonus : this.extraBonuses.get(holder)) {
                for (Supplier<GemBonus> bonus : extraBonus.bonuses()) {
                    try {
                        ((GemBonusExtension) gem).fallen_lib$appendExtraBonus(bonus.get());
                    } catch (Exception e) {
                        FallenLib.LOGGER.error("Failed applying extra gem bonus [{}]", registry.inverse().get(extraBonus));
                    }
                }
            }
        }
    }

    private void clearExtraGemBonuses() {
        for (Gem gem : GemRegistry.INSTANCE.getValues()) {
            if (gem instanceof GemBonusExtension extension) {
                extension.fallen_lib$clearExtraBonuses();
            }
        }
    }

    public record ExtraGemBonus(DynamicHolder<Gem> gem,
                                List<Supplier<GemBonus>> bonuses) implements CodecProvider<ExtraGemBonus>, ICodecProvider<ExtraGemBonus> {

        public static final Codec<Supplier<GemBonus>> SUPPLIER_CODEC = new SupplierCodec<>(GemBonus.CODEC);

        public static final Codec<ExtraGemBonus> CODEC = RecordCodecBuilder.create(inst -> inst
                .group(
                        GemRegistry.INSTANCE.holderCodec().fieldOf("gem").forGetter(ExtraGemBonus::gem),
                        SUPPLIER_CODEC.listOf().fieldOf("bonuses").forGetter(ExtraGemBonus::bonuses))
                .apply(inst, ExtraGemBonus::new));

        @Override
        public Codec<? extends ExtraGemBonus> getCodec() {
            return CODEC;
        }
    }
}