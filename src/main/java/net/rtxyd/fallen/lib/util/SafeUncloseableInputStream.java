package net.rtxyd.fallen.lib.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SafeUncloseableInputStream extends FilterInputStream {
    private boolean logicallyClosed = false;

    public SafeUncloseableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() throws IOException {
        logicallyClosed = true;
    }

    public boolean isLogicallyClosed() {
        return logicallyClosed;
    }
}