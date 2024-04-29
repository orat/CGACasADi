package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
//import de.orat.math.gacalc.caching.iFunctionSymbolicCache;

public class CGASymbolicFunctionCache //implements iFunctionSymbolicCache<SparseCGASymbolicMultivector, CachedSparseCGASymbolicMultivector>
{

    public static CGASymbolicFunctionCache instance() {
        return INSTANCE;
    }

    private static final CGASymbolicFunctionCache INSTANCE = new CGASymbolicFunctionCache();

    private CGASymbolicFunctionCache() {

    }

    private final Map<String, CGASymbolicFunction> functionCache = new HashMap<>(1024, 0.5f);
    private final Map<String, Integer> cachedFunctionsUsage = new HashMap<>(1024, 0.5f);

    //@Override
    public CachedSparseCGASymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<CachedSparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction func = functionCache.get(name);
        if (func == null) {
            final int size = args.size();
            List<CachedSparseCGASymbolicMultivector> params = new ArrayList<>(size);
            if (args.size() > PARAM_NAMES.length()) {
                throw new RuntimeException("Too many args given.");
            }
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // Convert to purely symbolic multivector.
                SparseCGASymbolicMultivector param = SparseCGASymbolicMultivector.create(
                    String.valueOf(PARAM_NAMES.charAt(i)), arg.getSparsity());
                params.add(new CachedSparseCGASymbolicMultivector(param));
            }
            func = new CGASymbolicFunction(name, params, List.of(res.apply(params)));
            functionCache.put(name, func);
            cachedFunctionsUsage.put(name, 0);
        }
        SparseCGASymbolicMultivector retVal = func.callSymbolic(args).get(0);
        cachedFunctionsUsage.compute(name, (k, v) -> ++v);
        return new CachedSparseCGASymbolicMultivector(retVal);
    }

    public void clearCache() {
        this.functionCache.clear();
        this.cachedFunctionsUsage.clear();
    }

    public Map<String, Integer> getUnmodifiableCachedFunctionsUsage() {
        return Collections.unmodifiableMap(this.cachedFunctionsUsage);
    }

    public SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage() {
        return new TreeMap<>(getUnmodifiableCachedFunctionsUsage());
    }

    public String cachedFunctionUsageToString() {
        SortedMap<String, Integer> cachedFunctionUsage = CGASymbolicFunctionCache.instance().getSortedUnmodifiableCachedFunctionsUsage();
        int maxKeyLength = cachedFunctionUsage.entrySet().stream()
            .mapToInt(entry -> entry.getKey().length())
            .max().orElse(0);
        String format = String.format("%%-%ss : %%s", maxKeyLength);
        return cachedFunctionUsage.entrySet().stream()
            .map(entry -> String.format(format, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));
    }

    private static final String PARAM_NAMES = "abcdef";

    /**
     * A valid CasADi function name starts with a letter followed by letters, numbers or non-consecutive
     * underscores. The cache names and CadADi function names do not need to be the same, but currently are.
     *
     * @param params Either iMultivectorSymbolic or int
     */
    public String createFuncName(String name, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("_");
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
                }
            } else if (param instanceof Integer intParam) {
                sb.append(intParam);
            } else {
                throw new RuntimeException("Param of unexpected type.");
            }
            sb.append("_");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
