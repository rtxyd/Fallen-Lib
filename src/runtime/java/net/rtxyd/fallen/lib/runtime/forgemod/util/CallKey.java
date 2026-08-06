package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.rtxyd.fallen.lib.util.call.ContextKey;

public class CallKey<T> extends ContextKey<T> {

    private int clientFingerprint = -1;
    private int serverFingerprint = -1;
    private int callCount = -1;

    protected CallKey(String id) {
        super(id);
    }

    void updateContext() {
        clientFingerprint = GameLifecycleHelper.clientCycleTickCount;
        serverFingerprint = GameLifecycleHelper.serverCycleTickCount;
    }

    void updateCallCount() {
        callCount++;
    }

    void resetCallCount() {
        callCount = -1;
    }

    public int getCallCount() {
        return callCount;
    }

    public int getClientFingerprint() {
        return clientFingerprint;
    }

    public int getServerFingerprint() {
        return serverFingerprint;
    }

    public boolean isInitialized() {
        return getCallCount() >= 0;
    }

    public boolean isCalled() {
        return getCallCount() > 0;
    }

    public boolean isFirst() {
        return getCallCount() == 0;
    }

    void init() {
        updateContext();
        callCount = 0;
    }
}
