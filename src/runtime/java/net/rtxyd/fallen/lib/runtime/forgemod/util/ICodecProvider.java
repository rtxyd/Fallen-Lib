package net.rtxyd.fallen.lib.runtime.forgemod.util;

import com.mojang.serialization.Codec;

public interface ICodecProvider<T> {
    Codec<? extends T> getCodec();
}
