package net.rtxyd.fallen.lib.type.util.patch;

public interface IInserterContext<T, R> {
    T receiver();
    R ret();

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

    boolean isInstanceCall();

    boolean isStaticCall();

    boolean isInvokeDynamic();

    boolean isVoidReturn();

    boolean isPrimitiveReturn();

    boolean willReplaceReturn();
}
