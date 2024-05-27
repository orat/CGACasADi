package de.orat.math.cgacasadi.caching.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import java.util.List;

public final class Classes {

    private Classes() {

    }

    public static final ClassName T_CGASymbolicFunctionCache = ClassName.get("de.orat.math.cgacasadi.caching", "CGASymbolicFunctionCache");
    public static final ClassName T_ISafePublicFunctionCache = ClassName.get("de.orat.math.cgacasadi.caching", "ISafePublicFunctionCache");
    public static final ClassName T_SX = ClassName.get("de.dhbw.rahmlab.casadi.impl.casadi", "SX");
    public static final ClassName T_iMultivectorSymbolic = ClassName.get("de.orat.math.gacalc.spi", "iMultivectorSymbolic");

    public static final ClassName T_String = ClassName.get(String.class);
    public static final ClassName T_Override = ClassName.get(Override.class);
    public static final ClassName T_List = ClassName.get(List.class);
}
