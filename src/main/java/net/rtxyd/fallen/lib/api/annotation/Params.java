package net.rtxyd.fallen.lib.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.CLASS)
public @interface Params {
    int[] catchOuterArgs();
    int modifyArg() default -1;
}
