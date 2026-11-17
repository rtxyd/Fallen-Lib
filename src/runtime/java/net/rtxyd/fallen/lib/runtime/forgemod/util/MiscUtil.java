package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class MiscUtil {
    public static <T> FriendlyByteBufCodec<T> createSingleStringBufCodec(Function<T, String> getter, Function<String, T> ctor) {
        return new FriendlyByteBufCodec<T>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull T value) {
                String s = getter.apply(value);
                buf.writeInt(s.length());
                buf.writeUtf(s);
            }

            @Override
            public @NotNull T decode(@NotNull FriendlyByteBuf buf) {
                int length = buf.readInt();
                String s = buf.readUtf(length);
                return ctor.apply(s);
            }
        };
    }
}
