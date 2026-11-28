package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.IHolderOwner;
import net.rtxyd.fallen.lib.runtime.forgemod.util.Serialization;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class AbstractPacketBoundRegistry<E extends ICodecProvider<? super E>>
        extends SimpleJsonResourceReloadListener implements IPacketBoundRegistry<E>, ICodecProvider<E>, IHolderOwner<ResourceLocation, E> {

    protected final String path;
    private final Logger logger;
    protected ICondition.IContext context;
    protected Predicate<ResourceLocation> locFilter;
    protected final String type;
    protected final boolean doSync;
    protected final boolean useTypeIdAsKey;

    protected BiMap<ResourceLocation, E> registry = ImmutableBiMap.of();
    protected Map<ResourceLocation, BoundHolder<E>> holders = new ConcurrentHashMap<>();
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends AbstractPacketBoundRegistry>, AbstractPacketBoundRegistry> SINGLETONS = new HashMap<>();

    private final BiMap<ResourceLocation, Codec<? extends E>> CODEC_MAP = HashBiMap.create();
    private Codec<E> fallbackCodec = null;

    protected final Map<ResourceLocation, E> temp = new HashMap<>();

    IRegistrySyncHandler<E> packetHandler;
    private Codec<E> singletonBoundCodec;
    private final Codec<BoundHolder<E>> holderCodec;
    private final StreamCodec<ByteBuf, BoundHolder<E>> holderStreamCodec;

    public AbstractPacketBoundRegistry(Logger logger, String path, String type, Predicate<ResourceLocation> locFilter, boolean doSync, boolean useTypeIdAsKey) {
        super(new Gson(), path);
        this.path = path;
        this.logger = logger;
        this.type = type;
        this.doSync = doSync;
        this.locFilter = locFilter;
        this.useTypeIdAsKey = useTypeIdAsKey;
        this.initSingletonBoundCodec();
        this.registerBuiltinCodecs();
        this.holderCodec = ResourceLocation.CODEC.xmap(this::holder, BoundHolder::getId);
        this.holderStreamCodec = ResourceLocation.STREAM_CODEC.map(this::holder, BoundHolder::getId);
    }

    final void initPacketsHandler(IRegistrySyncHandler<E> constructors) {
        if (this.packetHandler == null) {
            this.validateHandler(constructors);
            this.packetHandler = constructors;
        }
    }

    private final void validateHandler(IRegistrySyncHandler<E> constructors) {
        Objects.requireNonNull(constructors, "Registry[" + this.getClass() + "]: Invalid packet constructors!");
    }

    public final void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        if (this.context != null) return;
        this.context = event.getConditionContext();
    }

    final void registerCommon() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, this::onAddReloadListeners);
    }

    final void registerSync(EventPriority priority) {
        NeoForge.EVENT_BUS.addListener(priority, this::syncClient);
    }

    @Override
    @SuppressWarnings("unchecked")
    // placebo way to parse
    protected final void apply(Map<ResourceLocation, JsonElement> map, ResourceManager provider, ProfilerFiller p_10795_) {
        if (!this.validate()) {
            return;
        }
        this.beginReload(ReloadType.SERVER);
        Codec<E> codec = Objects.requireNonNull(this.singletonBoundCodec, "Codec is null or not registered in " + path);
        ConditionalOps<JsonElement> ops = this.makeConditionalOps();
        map.forEach((key, ele) -> {
            if (!locFilter.test(key)) return;
            try {
                if (Serialization.isNotNullOrIsJsonObj(ele, key, this.path, this.logger)) {
                    JsonObject obj = ele.getAsJsonObject();
                    if (Serialization.checkEmptyJsonObjAndLog(obj, key, this.path, this.logger) && Serialization.checkConditions(obj, key, this.path, this.logger, ops)) {
                        E deserialized = codec.decode(ops, obj).getOrThrow().getFirst();
                        if (useTypeIdAsKey) {
                            JsonElement id = ops.get(obj, type).getOrThrow();
                            ResourceLocation keyTypeId = ResourceLocation.CODEC.decode(ops, id).getOrThrow().getFirst();
                            this.register(keyTypeId, deserialized);
                        } else {
                            this.register(key, deserialized);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Failed parsing {} file {}.", this.path, key);
                logger.error("Underlying Exception: ", e);
            }
        });
        this.onReload(ReloadType.SERVER);
    }

    private final void register(ResourceLocation loc, E item) {
        if (this.registry.containsKey(loc)) throw new UnsupportedOperationException("Duplicated id: [" + loc + "]");
        registerTempEntry(loc, item);
        this.registry.put(loc, item);
        this.holders.computeIfAbsent(loc, a -> new BoundHolder<>(loc, this));
    }

    public boolean validate() {
        if (this.packetHandler == null && this.doSync) {
            throw new UnsupportedOperationException("Registry [" + this.getClass() +  "] is intended to do sync, but bound constructors are not initialized!");
        }
        if (!SINGLETONS.containsKey(this.getClass())) {
            throw new UnsupportedOperationException("Registry [" + this.getClass() +  "] is intended to do reload, but it's not registered!");
        }
        if (CODEC_MAP.isEmpty()) {
            throw new UnsupportedOperationException("Registry [" + this.getClass() +  "] should have at least 1 registered codec!");
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <A extends ICodecProvider<? super A>>
    AbstractPacketBoundRegistry<A> getSingleton(Class<? extends AbstractPacketBoundRegistry<A>> registryClass) {
        return (AbstractPacketBoundRegistry<A>) SINGLETONS.get(registryClass);
    }

    @SuppressWarnings("rawtypes")
    static void registerSingleton(AbstractPacketBoundRegistry instance) {
        SINGLETONS.computeIfAbsent(instance.getClass(), k -> instance);
    }

    public final void registerCodec(ResourceLocation loc, Codec<? extends E> codec) {
        CODEC_MAP.put(Objects.requireNonNull(loc), codec);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    // placebo way codec
    final Codec<E> initSingletonBoundCodec() {
        if (this.singletonBoundCodec == null) {
            this.singletonBoundCodec = new Codec<>() {
                @Override
                public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
                    Optional<T> id = ops.get(input, type).resultOrPartial(str -> {});
                    Optional<ResourceLocation> key = id.map(t -> ResourceLocation.CODEC.decode(ops, t).resultOrPartial(logger::error).get().getFirst());

                    Codec codec = key.<Codec>map(CODEC_MAP::get).orElse(fallbackCodec);

                    if (codec == null) {
                        return DataResult.error(() -> "Failed parsing [" + id.get() + "] Obj: " + input);
                    }
                    return codec.decode(ops, input);
                }

                @Override
                public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
                    Codec<E> codec = (Codec<E>) input.getCodec();
                    ResourceLocation key = CODEC_MAP.inverse().get(codec);
                    if (key == null) {
                        return DataResult.error(() -> "Codec is not registered! Obj:" + input);
                    }
                    T resultKey = ResourceLocation.CODEC.encodeStart(ops, key).getOrThrow();
                    T resultObj = codec.encode(input, ops, prefix).getOrThrow();
                    return ops.mergeToMap(resultObj, ops.createString(type), resultKey);
                }
            };
        }
        return this.singletonBoundCodec;
    }

    protected abstract void registerBuiltinCodecs();
    protected final void registerFallbackCodec(Codec<E> fallback) {
        this.fallbackCodec = fallback;
    }

    public Set<ResourceLocation> getKeys() {
        return this.registry.keySet();
    }

    public Collection<E> getValues() {
        return this.registry.values();
    }

    @Nullable
    public E fallen_lib$getValue(ResourceLocation key) {
        return getValue(key);
    }

    @Nullable
    public E getValue(ResourceLocation key) {
        return this.registry.get(key);
    }

    @Nullable
    public ResourceLocation getKey(E value) {
        return this.registry.inverse().get(value);
    }

    public E getOrDefault(ResourceLocation key, E defValue) {
        return this.registry.getOrDefault(key, defValue);
    }

    public BoundHolder<E> holder(ResourceLocation id) {
        return this.holders.computeIfAbsent(id, r -> new BoundHolder<>(id, this));
    }

    public BoundHolder<E> holder(E t) {
        ResourceLocation key = getKey(t);
        return holder(key == null ? BoundHolder.EMPTY : key);
    }

    public BoundHolder<E> emptyHolder() {
        return holder(BoundHolder.EMPTY);
    }

    // serverside
    @Override
    public final void syncClient(OnDatapackSyncEvent e) {
        if (packetHandler == null) {
            throw new UnsupportedOperationException("Registry[" + this.getClass() + "] packet constructors are not initialized!");
        }
        ServerPlayer player = e.getPlayer();
        if (player == null) {
            packetHandler.begin(null);
            registry.forEach((path, item) -> {
                packetHandler.process(null, path, item);
            });
            packetHandler.end(null);
        } else {
            packetHandler.begin(player);
            registry.forEach((path, item) -> {
                packetHandler.process(player, path, item);
            });
            packetHandler.end(player);
        }
    }

    @Override
    public void beginReload(ReloadType type) {
        this.registry = HashBiMap.create();
        this.holders.values().forEach(BoundHolder::reset);
    }

    @Override
    public void onReload(ReloadType type) {
        this.registry = ImmutableBiMap.copyOf(this.registry);
        this.holders.values().forEach(BoundHolder::bind);
    }

    @Override
    public final void beginSync() {
        this.temp.clear();
    }

    @Override
    public final void registerTempEntry(ResourceLocation loc, E item) {
        this.validateItem(loc, item);
        this.temp.put(loc, item);
    }

    @Override
    public void validateItem(ResourceLocation loc, E item) {}

    @Override
    public final void handleBegin(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(this::beginSync);
    }

    @Override
    public final void handleProcess(IPayloadContext contextSupplier, ResourceLocation path, E item) {
        contextSupplier.enqueueWork(() -> {
            this.registerTempEntry(path, item);
        });
    }

    private void onDedicatedClient() {
        normalEndProcess(ReloadType.DEDICATED_CLIENT);
    }

    private void onIntegratedClient() {
        this.temp.clear();
        this.temp.putAll(this.registry);
        normalEndProcess(ReloadType.INTEGRATED_CLIENT);
    }

    private void normalEndProcess(ReloadType type) {
        this.beginReload(type);
        this.applyTemp();
        this.onReload(type);
    }

    @Override
    public final void handleEnd(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                onIntegratedClient();
                return;
            }
            onDedicatedClient();
        });
    }

    @Override
    public void applyTemp() {
        temp.forEach((k,v)->{
            registry.put(k, v);
            this.holders.computeIfAbsent(k, r -> new BoundHolder<>(r, this));
        });
    }

    @Override
    public final Codec<E> getCodec() {
        return this.singletonBoundCodec;
    }

    public final Codec<E> getFallbackCodec() {
        return this.fallbackCodec;
    }

    public final Codec<BoundHolder<E>> getHolderCodec() {
        return holderCodec;
    }

    public StreamCodec<ByteBuf, BoundHolder<E>> getHolderStreamCodec() {
        return holderStreamCodec;
    }
}
