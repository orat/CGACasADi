package de.orat.math.cgacasadi.caching.annotation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateCached {

    boolean warnFailedToCache() default true;

    boolean warnUncached() default false;
}
