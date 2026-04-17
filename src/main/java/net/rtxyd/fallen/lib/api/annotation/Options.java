package net.rtxyd.fallen.lib.api.annotation;

import net.rtxyd.fallen.lib.util.patch.PatchOption;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.CLASS)
public @interface Options {
    PatchOption[] options() default {};
}
