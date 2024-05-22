package de.orat.math.cgacasadi.caching.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.caching.CGASymbolicFunctionCache;
import java.util.List;

public final class Classes {

    private Classes() {

    }

    public static final ClassName T_CGASymbolicFunctionCache = ClassName.get(CGASymbolicFunctionCache.class);
    public static final ClassName T_String = ClassName.get(String.class);
    public static final ClassName T_SX = ClassName.get(SX.class);
    public static final ClassName T_Override = ClassName.get(Override.class);
    public static final ClassName T_List = ClassName.get(List.class);
}
