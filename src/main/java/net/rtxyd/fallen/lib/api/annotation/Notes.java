package net.rtxyd.fallen.lib.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface Notes {
    Class<?>[] argTypes() default {};
    Class<?>[] receiverType() default {};
    Class<?>[] retType() default {};
}
