package de.orat.math.cgacasadi.caching;

import de.orat.math.cgacasadi.impl.CGASymbolicFunction;
import de.orat.math.cgacasadi.impl.PurelySymbolicCachedSparseCGASymbolicMultivector;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
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

/**
 * No check for function name collisions. Especially hazardous if an instance is used by more than one class.
 */
public class CGASymbolicFunctionCache implements ISafePublicFunctionCache {

    private final Map<String, CGASymbolicFunction> functionCache
        = new HashMap<>(1024, 0.5f);
    private final Map<String, Integer> cachedFunctionsUsage
        = new HashMap<>(1024, 0.5f);

    public CachedSparseCGASymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<? extends CachedSparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction func = functionCache.get(name);
        if (func == null) {
            final int size = args.size();
            List<PurelySymbolicCachedSparseCGASymbolicMultivector> params = new ArrayList<>(size);
            if (args.size() > PARAM_NAMES.length()) {
                throw new RuntimeException("Too many args given.");
            }
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // Convert to purely symbolic multivector.
                // grades
                // Is already a CachedSparseCGASymbolicMultivector.
                PurelySymbolicCachedSparseCGASymbolicMultivector param = new PurelySymbolicCachedSparseCGASymbolicMultivector(
                    String.valueOf(PARAM_NAMES.charAt(i)), arg.grades());
                // sparsity
//                PurelySymbolicCachedSparseCGASymbolicMultivector param = new PurelySymbolicCachedSparseCGASymbolicMultivector(
//                    String.valueOf(PARAM_NAMES.charAt(i)), arg.getSparsity());
                // dense
//                PurelySymbolicCachedSparseCGASymbolicMultivector param = new PurelySymbolicCachedSparseCGASymbolicMultivector(
//                    String.valueOf(PARAM_NAMES.charAt(i)));
                //
                params.add(param);
            }
            func = new CGASymbolicFunction(name, params, List.of(res.apply(params)));
            functionCache.put(name, func);
            cachedFunctionsUsage.put(name, 0);
        }
        // Is already a CachedSparseCGASymbolicMultivector.
        SparseCGASymbolicMultivector retVal = func.callSymbolic(args).get(0);
        cachedFunctionsUsage.compute(name, (k, v) -> ++v);
        return new CachedSparseCGASymbolicMultivector(retVal);
    }

    @Override
    public void clearCache() {
        this.functionCache.clear();
        this.cachedFunctionsUsage.clear();
    }

    @Override
    public Map<String, Integer> getUnmodifiableCachedFunctionsUsage() {
        return Collections.unmodifiableMap(this.cachedFunctionsUsage);
    }

    @Override
    public SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage() {
        return new TreeMap<>(getUnmodifiableCachedFunctionsUsage());
    }

    @Override
    public String cachedFunctionUsageToString() {
        SortedMap<String, Integer> cachedFunctionUsage = getSortedUnmodifiableCachedFunctionsUsage();
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
                // grades
                int[] grades = mv.grades();
                for (int i = 0; i < grades.length; i++) {
                    sb.append(grades[i]);
                }
                // sparsity
//                String colind = Arrays.stream(mv.getSparsity().getcolind())
//                    .mapToObj((int i) -> String.valueOf(i))
//                    .collect(Collectors.joining("_"));
//                String row = Arrays.stream(mv.getSparsity().getrow())
//                    .mapToObj((int i) -> String.valueOf(i))
//                    .collect(Collectors.joining("_"));
//                sb.append("_colind_").append(colind);
//                sb.append("_row_").append(row);
                // dense
                // Comment out: GACalcAPI::MultivectorSymbolic::scalarSqrt()::IllegalArgumentException
                //
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
