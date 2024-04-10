package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
//import de.orat.math.gacalc.caching.iFunctionSymbolicCache;

public class CGASymbolicFunctionCache //implements iFunctionSymbolicCache<SparseCGASymbolicMultivector, CachedSparseCGASymbolicMultivector>
{

    public static CGASymbolicFunctionCache instance() {
        return INSTANCE;
    }

    private static final CGASymbolicFunctionCache INSTANCE = new CGASymbolicFunctionCache();

    private CGASymbolicFunctionCache() {

    }

    private final Map<String, CGASymbolicFunction> functionCache = new HashMap<>();
    private final Map<String, Integer> usageCache = new HashMap<>();

    //@Override
    public CachedSparseCGASymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<CachedSparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction func = functionCache.get(name);
        if (func == null) {
            final int size = args.size();
            List<CachedSparseCGASymbolicMultivector> params = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // Convert to purely symbolic multivector.
                SparseCGASymbolicMultivector param = SparseCGASymbolicMultivector.create(Integer.toString(i), arg.getSparsity());
                params.add(new CachedSparseCGASymbolicMultivector(param));
            }
            func = new CGASymbolicFunction(name, params, List.of(res.apply(params)));
            functionCache.put(name, func);
            usageCache.put(name, 0);
        }
        SparseCGASymbolicMultivector retVal = func.callSymbolic(args).get(0);
        usageCache.compute(name, (k, v) -> ++v);
        return new CachedSparseCGASymbolicMultivector(retVal);
    }

    //@Override
    public Map<String, Integer> getCachedFunctionUsage() {
        return Collections.unmodifiableMap(this.usageCache);
    }

    private static final String PARAM_NAMES = "abcdef";

    /**
     * special characters like ._-[]{} are not allowed as function names
     *
     * @param params Either iMultivectorSymbolic or int
     */
    public String createFuncName(String name, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (params.length > PARAM_NAMES.length()) {
            throw new RuntimeException("Too many params given.");
        }
        for (int paramIndex = 0; paramIndex < params.length; ++paramIndex) {
            sb.append(PARAM_NAMES.charAt(paramIndex));
            Object param = params[paramIndex];
            if (param instanceof iMultivectorSymbolic mv) {
                int[] grades = mv.grades();
                for (int i = 0; i < grades.length; i++) {
                    sb.append(grades[i]);
                    //sb.append("-");
                }
            } else if (param instanceof Integer intParam) {
                sb.append(intParam);
            } else {
                throw new RuntimeException("Param of unexpected type.");
            }
        }
        return sb.toString();
    }
}
