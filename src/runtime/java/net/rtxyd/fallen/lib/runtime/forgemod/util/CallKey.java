package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.rtxyd.fallen.lib.util.call.ContextKey;

public class CallKey<T> extends ContextKey<T> {

    private final CallContext context = new CallContext();

    protected CallKey(String id) {
        super(id);
    }

    void updateContext() {
        context.clientFingerprint = GameLifecycleHelper.clientCycleTickCount;
        context.serverFingerprint = GameLifecycleHelper.serverCycleTickCount;
    }

    void updateCallCount() {
        context.callCount++;
    }

    void resetCallCount() {
        context.callCount = -1;
    }

    int getCallCount() {
        return context.callCount;
    }

    int getClientFingerprint() {
        return context.clientFingerprint;
    }

    int getServerFingerprint() {
        return context.serverFingerprint;
    }

    void init() {
        context.init();
    }

    public static class CallContext {
        private int clientFingerprint = -1;
        private int serverFingerprint = -1;
        private int callCount = -1;

        public CallContext() {}
        void init() {
            this.clientFingerprint = GameLifecycleHelper.clientCycleTickCount;
            this.serverFingerprint = GameLifecycleHelper.serverCycleTickCount;
            this.callCount = 0;
        }
    }
}
