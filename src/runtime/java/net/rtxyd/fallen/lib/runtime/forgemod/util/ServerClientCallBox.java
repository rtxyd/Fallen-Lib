package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.rtxyd.fallen.lib.util.call.BiThreadCallBox;

public class ServerClientCallBox extends BiThreadCallBox {
    @Override
    protected void initThread(int index, Thread thread) {}

    void setThread(int index, Thread thread) {
        this.threads[index] = thread;
    }

    public boolean isThread0() {
        return this.threads[0] == Thread.currentThread();
    }
}
