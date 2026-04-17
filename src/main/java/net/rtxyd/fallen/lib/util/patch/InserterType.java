package net.rtxyd.fallen.lib.util.patch;

public enum InserterType {


    /**
     * standard is for modifying return value, or just catch all args, receiver, and return value, can catch outer args,
     * if the return value is void, may cause an exception
     */
    STANDARD(Expected.EXPECTED_STANDARD),
    /**
     * standard_void can't modify return, just catch all args, receiver, and return value, can catch outer args
     */
    STANDARD_VOID(Expected.EXPECTED_STANDARD_VOID),
//    BEFORE_CANCELLABLE(Expected.EXPECTED_BEFORE_CANCELLABLE),
    /**
     * before_modify_arg is inserted before the invoking, can modify arg, can catch outer args, can return void.
     */
    BEFORE_MODIFY_ARG(Expected.EXPECTED_BEFORE_MODIFY_ARG);
//    AFTER();



    private static final String STANDARD_STARTER = "(Lnet/rtxyd/fallen/lib/type/util/patch/IInserterContext;[Ljava/lang/Object;)";

    private final String expected;

    InserterType(String expected1) {
        this.expected = expected1;
    }

    public static String standardStarter() {
        return STANDARD_STARTER;
    }

    public String getExpected() {
        return expected;
    }

    public static class Expected {
        public static final String EXPECTED_STANDARD = "public static RET hook(IInserterContext<REC, RET> ctx, Object... args)";
        public static final String EXPECTED_STANDARD_VOID = "public static void hook(IInserterContext<REC, RET> ctx, Object... args)";
//        public static final String EXPECTED_BEFORE_CANCELLABLE = "public static boolean hook(IInserterContext<Object, Object> ctx, Object... args)";
        public static final String EXPECTED_BEFORE_MODIFY_ARG = "public static ARG_TYPE hook(IInserterContext<REC, RET> ctx, Object... args)";
    }
}