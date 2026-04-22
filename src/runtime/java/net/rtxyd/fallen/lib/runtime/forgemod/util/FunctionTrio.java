package net.rtxyd.fallen.lib.runtime.forgemod.util;

import java.util.function.Function;

public record FunctionTrio<O, R, S> (Function<O, R> O2R, Function<R, S> R2S, Function<S, R> S2R) {}
