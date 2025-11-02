package de.orat.math.cgacasadi.caching.annotation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Doc: https://github.com/orat/CGACasADi/blob/master/CGACasADi_SymbolicMultivectorCachingProcessor/README.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Uncached {
}
