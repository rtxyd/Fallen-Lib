package net.rtxyd.fallen.lib.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class OuterTrackedInputStream extends FilterInputStream {

    private final AtomicBoolean realClosed;

    public OuterTrackedInputStream(InputStream in, AtomicBoolean realClosed) {
        super(in);
        this.realClosed = realClosed;
    }

    public boolean isClosed() {
        return realClosed.get();
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        } finally {
            realClosed.set(true);
        }
    }
}