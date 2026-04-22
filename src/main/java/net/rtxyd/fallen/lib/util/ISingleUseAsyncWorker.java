package net.rtxyd.fallen.lib.util;

import java.util.Optional;
import java.util.function.Consumer;

public interface ISingleUseAsyncWorker extends IAsyncWorker {

    /**
     * Use {@link ISingleUseAsyncWorker#readAndBurn(String, Consumer)}
     */
    @Override
    @Deprecated
    public <T> Optional<T> getResult(String id, Consumer<Exception> processEx);

    /**
     * Use {@link ISingleUseAsyncWorker#readAndBurnOrThrow(String)}
     */
    @Override
    @Deprecated
    <T> T getResultOrThrow(String id);

    <T> Optional<T> readAndBurn(String id, Consumer<Exception> processEx);

    <T> T readAndBurnOrThrow(String id);
}
