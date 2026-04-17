package net.rtxyd.fallen.lib.util.patch;

import net.rtxyd.fallen.lib.type.util.patch.IInserterContext;

public class InserterContext<REC, RET> implements IInserterContext<REC, RET> {
    private final REC receiver;
    private final RET ret;
    private final int flags;
    private final int lastOuterArg;

    public static final int F_INSTANCE        = 1 << 0;
    public static final int F_INVOKESTATIC    = 1 << 1;

    public static final int F_RET_VOID        = 1 << 2;
    public static final int F_RET_PRIMITIVE   = 1 << 3;
    public static final int F_REPLACE_RET     = 1 << 4;
    public static final int F_STRICT_RET      = 1 << 5;

    public static final int F_INVOKEINTERFACE = 1 << 6;
    public static final int F_INVOKESPECIAL   = 1 << 7;
    public static final int F_INVOKEVIRTUAL   = 1 << 8;
    public static final int F_INVOKEDYNAMIC   = 1 << 9;


    public InserterContext(REC receiver, RET ret, int flags) {
        this.receiver = receiver;
        this.ret = ret;
        this.flags = flags;
        this.lastOuterArg = -1;
    }

    public InserterContext(REC receiver, RET ret, int flags, int lastOuterArg) {
        this.receiver = receiver;
        this.ret = ret;
        this.flags = flags;
        this.lastOuterArg = lastOuterArg;
    }

    @Override
    public REC receiver() {
        return receiver;
    }

    @Override
    public RET ret() {
        return ret;
    }

    @Override
    public boolean isInstanceCall() {
        return (flags & F_INSTANCE) != 0;
    }
    @Override
    public boolean isStaticCall() {
        return (flags & F_INVOKESTATIC) != 0;
    }
    @Override
    public boolean isInvokeDynamic() {
        return (flags & F_INVOKEDYNAMIC) != 0;
    }
    @Override
    public boolean isVoidReturn() {
        return (flags & F_RET_VOID) != 0;
    }
    @Override
    public boolean isPrimitiveReturn() {
        return (flags & F_RET_PRIMITIVE) != 0;
    }
    @Override
    public boolean willReplaceReturn() {
        return (flags & F_REPLACE_RET) != 0;
    }
}
