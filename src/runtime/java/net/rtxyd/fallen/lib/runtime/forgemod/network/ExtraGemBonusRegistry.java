package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.google.common.base.Predicates;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import dev.shadowsoffire.placebo.reload.RegistryCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.GemBonusExtension;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.SupplierCodec;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExtraGemBonusRegistry extends AbstractLazyPacketBoundRegistry<ExtraGemBonusRegistry.ExtraGemBonus, ExtraGemBonusPayload.Begin, ExtraGemBonusPayload, ExtraGemBonusPayload.End> {
    public static final ExtraGemBonusRegistry INSTANCE = new ExtraGemBonusRegistry();

    protected Multimap<DynamicHolder<Gem>, ExtraGemBonus> extraBonuses = HashMultimap.create();

    static boolean isGemRegistryChecked = false;

    public static final RegistryCallback<Gem> CHECK_CALLBACK = new RegistryCallback<Gem>() {
        @Override
        public void beginReload(DynamicRegistry manager) {
            // do nothing
        }

        @Override
        public void onReload(DynamicRegistry manager) {
            isGemRegistryChecked = true;
        }
    };

    RegistryCallback<Gem> loadingCallback;

    static boolean isBeforeGemRegistry = false;

    public ExtraGemBonusRegistry() {
        super(FallenLib.LOGGER, "extra_gem_bonuses", "type", Predicates.alwaysTrue(), true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerCodec(ResourceLocation.fromNamespaceAndPath(FallenLib.MODID, "extra_gem_bonus"), ExtraGemBonus.CODEC);

        // backward compatibility for lib version 1.3.2
        if (ModList.get().isLoaded("fallen_gems_affixes")) {
            this.registerCodec(ResourceLocation.fromNamespaceAndPath("fallen_gems_affixes", "extra_gem_bonus"), Codec.of(ExtraGemBonusRegistry.ExtraGemBonus.CODEC, ExtraGemBonusRegistry.ExtraGemBonus.CODEC));
        }
        //end
    }

    @Override
    public void beginReload() {
        super.beginReload();
        this.extraBonuses = HashMultimap.create();
    }

    public void beginReloadDelayed() {
        this.registry = HashBiMap.create();
        this.extraBonuses = HashMultimap.create();
    }

    public void addGemRegistryCheckCallback() {
        FallenLib.LOGGER.info("Register ExtraGemBonus check callback for GemRegistry to ensure loading.");
        GemRegistry.INSTANCE.addCallback(CHECK_CALLBACK);
    }

    public void addGemRegistryLoadingCallback() {
        FallenLib.LOGGER.info("Register ExtraGemBonus loading callback for GemRegistry to ensure loading.");
        if (this.loadingCallback == null) {
            this.loadingCallback = new RegistryCallback<Gem>() {
                @Override
                public void beginReload(DynamicRegistry manager) {
                    // do nothing
                }

                @Override
                public void onReload(DynamicRegistry manager) {
                    beginReloadDelayed();
                    clearExtraGemBonuses();
                    load();
                    applyExtraGemBonuses();
                    FallenLib.LOGGER.info("Loading complete with {} entries", extraBonuses.size());
                }
            };
        }
        GemRegistry.INSTANCE.addCallback(this.loadingCallback);
    }

    public enum State {
        BEFORE_GEM_REGISTRY,
        AFTER_GEM_REGISTRY
    }

    @Override
    public void onReload() {
        super.onReload();
        // if check callback is not executed, then the GemRegistry is loaded before extra gem bonus
        // if it's executed, here we get nothing.
        // !check => beforeGemRegistry = true
        GemRegistry.INSTANCE.removeCallback(CHECK_CALLBACK);
        if (isBeforeGemRegistry || !isGemRegistryChecked) {
            isBeforeGemRegistry = true;
            if (this.loadingCallback != null) {
                GemRegistry.INSTANCE.removeCallback(this.loadingCallback);
            }
            addGemRegistryLoadingCallback();
        } else {
            FallenLib.LOGGER.info("Execute common ensure loading");
            this.clearExtraGemBonuses();
            this.load();
            this.applyExtraGemBonuses();
            FallenLib.LOGGER.info("Loading complete with {} entries", extraBonuses.size());
        }
    }

    private void load() {
        FallenLib.LOGGER.info("Loading extra gem bonus...");
        if (!lazyRegistry.isEmpty()) {
            for (Map.Entry<ResourceLocation, Supplier<ExtraGemBonus>> en : lazyRegistry.entrySet()) {
                Supplier<ExtraGemBonus> sup = en.getValue();
                try {
                    this.registry = HashBiMap.create(this.registry);
                    ExtraGemBonus extraGemBonus = sup.get();
                    this.registry.put(en.getKey(), extraGemBonus);
                } catch (Exception e) {
                    FallenLib.LOGGER.error("Failed parsing extra gem bonus [{}]", en.getKey());
                }
            }
        }
        for (ExtraGemBonus extraBonus : registry.values())
            this.extraBonuses.put(extraBonus.gem, extraBonus);
        this.registry = ImmutableBiMap.copyOf(this.registry);
        FallenLib.LOGGER.info("Finalize loading...");
    }

    private void applyExtraGemBonuses() {
        FallenLib.LOGGER.info("Current GemRegistry size [{}]", GemRegistry.INSTANCE.getKeys().size());
        for (Gem gem : GemRegistry.INSTANCE.getValues()) {
            DynamicHolder<Gem> holder = GemRegistry.INSTANCE.holder(gem);

            for (ExtraGemBonus extraBonus : this.extraBonuses.get(holder)) {
                for (GemBonus bonus : extraBonus.bonuses()) {
                    try {
                        ((GemBonusExtension) gem).fallen_lib$appendExtraBonus(bonus);
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
                                List<GemBonus> bonuses) implements CodecProvider<ExtraGemBonus>, ICodecProvider<ExtraGemBonus> {

        public static final Codec<Supplier<GemBonus>> SUPPLIER_CODEC = new SupplierCodec<>(GemBonus.CODEC);

        public static final Codec<ExtraGemBonus> CODEC = RecordCodecBuilder.create(inst -> inst
                .group(
                        GemRegistry.INSTANCE.holderCodec().fieldOf("gem").forGetter(ExtraGemBonus::gem),
                        GemBonus.CODEC.listOf().fieldOf("bonuses").forGetter(ExtraGemBonus::bonuses))
                .apply(inst, ExtraGemBonusRegistry.ExtraGemBonus::new));

        @Override
        public Codec<? extends ExtraGemBonus> getCodec() {
            return CODEC;
        }
    }
}
